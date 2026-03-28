package com.vunh.jetpack.bhx.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.jetpack.bhx.data.local.ProfileManager
import com.vunh.jetpack.bhx.domain.model.Category
import com.vunh.jetpack.bhx.domain.model.Post
import com.vunh.jetpack.bhx.domain.model.Product
import com.vunh.jetpack.bhx.domain.model.UserProfile
import com.vunh.jetpack.bhx.domain.usecase.GetDummyCategoriesUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetDummyProductsByCategoryUseCase
import com.vunh.jetpack.bhx.domain.usecase.GetEscuelaProductsUseCase
import com.vunh.jetpack.bhx.domain.usecase.ObservePostsUseCase
import com.vunh.jetpack.bhx.domain.usecase.SyncPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observePostsUseCase: ObservePostsUseCase,
    private val syncPostsUseCase: SyncPostsUseCase,
    private val getEscuelaProductsUseCase: GetEscuelaProductsUseCase,
    private val getDummyCategoriesUseCase: GetDummyCategoriesUseCase,
    private val getDummyProductsByCategoryUseCase: GetDummyProductsByCategoryUseCase,
    private val profileManager: ProfileManager
) : ViewModel() {

    // Trực tiếp sử dụng profileFlow từ ProfileManager để đảm bảo UI luôn cập nhật tự động
    val userProfile: StateFlow<UserProfile?> = profileManager.profileFlow

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _productByCategories = MutableStateFlow<List<Product>>(emptyList())
    val productByCategories: StateFlow<List<Product>> = _productByCategories.asStateFlow()

    private val _productSectionTitle = MutableStateFlow("SẢN PHẨM MỚI (ESCUELA API)")
    val productSectionTitle: StateFlow<String> = _productSectionTitle.asStateFlow()

    private val _selectedCategorySlug = MutableStateFlow("all")
    val selectedCategorySlug: StateFlow<String> = _selectedCategorySlug.asStateFlow()

    private val _dummyCategories = MutableStateFlow<List<Category>>(emptyList())
    val dummyCategories: StateFlow<List<Category>> = _dummyCategories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Observe posts from database
        viewModelScope.launch {
            observePostsUseCase().collect { posts ->
                _posts.value = posts
            }
        }

        refreshAll(showRefreshIndicator = false)
    }

    fun refreshAll() {
        refreshAll(showRefreshIndicator = true)
    }

    private fun refreshAll(showRefreshIndicator: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _isRefreshing.value = showRefreshIndicator
            _errorMessage.value = null
            try {
                supervisorScope {
                    val jobs = listOf(
                        async {
                            runCatching {
                                syncPostsUseCase()
                            }.onFailure(::handleRefreshError)
                        },
                        async {
                            runCatching {
                                getEscuelaProductsUseCase(limit = 10, offset = 0)
                            }.onSuccess { escuelaProducts ->
                                _products.value = escuelaProducts
                                _productSectionTitle.value = "SẢN PHẨM MỚI (ESCUELA API)"
                            }.onFailure(::handleRefreshError)
                        },
                        async {
                            runCatching {
                                getDummyCategoriesUseCase()
                            }.onSuccess { dummyCats ->
                                _dummyCategories.value = dummyCats
                            }.onFailure(::handleRefreshError)
                        },
                        async {
                            runCatching {
                                getDummyProductsByCategoryUseCase(
                                    name = "all",
                                    limit = 10,
                                    offset = 0
                                )
                            }.onSuccess { allProducts ->
                                _productByCategories.value = allProducts
                                _selectedCategorySlug.value = "all"
                                _productSectionTitle.value = "DUMMYJSON PRODUCTS: ALL"
                            }.onFailure(::handleRefreshError)
                        }
                    )
                    jobs.awaitAll()
                }
            } catch (e: Exception) {
                handleRefreshError(e)
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    fun refreshPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                syncPostsUseCase()
            } catch (e: Exception) {
                handleRefreshError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getEscuelaProductsUseCase(limit = 10, offset = 0)
                _products.value = result
                _productSectionTitle.value = "SẢN PHẨM MỚI (ESCUELA API)"
            } catch (e: Exception) {
                handleRefreshError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDummyProductsByCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _selectedCategorySlug.value = category.slug
                val result = getDummyProductsByCategoryUseCase(
                    name = category.slug,
                    limit = 10,
                    offset = 0
                )
                _productByCategories.value = result
                _productSectionTitle.value = if (category.slug == "all") {
                    "DUMMYJSON PRODUCTS: ALL"
                } else {
                    "DUMMYJSON PRODUCTS: ${category.name.replaceFirstChar { it.uppercase() }}"
                }
            } catch (e: Exception) {
                handleRefreshError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDummyCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getDummyCategoriesUseCase()
                _dummyCategories.value = result
            } catch (e: Exception) {
                handleRefreshError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleRefreshError(throwable: Throwable) {
        _errorMessage.value = when (throwable) {
            is UnknownHostException -> "Không có kết nối mạng. Vui lòng kiểm tra Internet và thử lại."
            else -> throwable.message ?: "Unable to load data"
        }
    }
}
