package com.vunh.jetpack.bhx.domain.model

data class BannerItem(
    val id: String,
    val title: String,
    val description: String,
    val actionText: String
)

data class PromoItem(
    val id: String,
    val title: String,
    val backgroundColorHex: String
)

data class HomeData(
    val banners: List<BannerItem>,
    val promoGrids: List<PromoItem>,
    val categories: List<CategoryItem>,
    val essentialProducts: List<ProductItem>,
    val dailyMarket: DailyMarketData
)

data class DailyMarketData(
    val title: String,
    val subtitle: String,
    val tabs: List<MarketTab>,
    val recipes: List<Recipe>,
    val freshProducts: List<FreshProduct>
)
