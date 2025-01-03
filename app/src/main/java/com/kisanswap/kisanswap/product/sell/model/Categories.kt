package com.kisanswap.kisanswap.dataClass

import com.kisanswap.kisanswap.R

data class Category(
    val name: String,
    val route: String,
    val synonyms: List<String> = emptyList(),
    val icon: Int,
    val specifications: List<CategorySpecification> = emptyList(),
    val subcategories: List<Category> = emptyList()
)

data class CategorySpecification(
    val specification: Pair<String, String>,
    val amount: String,
    val units: List<String>? = null
)

val productCategories = listOf(
    Category(
        name = "Old Items",
        route = "",
        icon = R.drawable.baseline_agriculture_24,
        subcategories = listOf(
            Category(
                name = "Cultivation",
                route = "",
                icon = R.drawable.baseline_agriculture_24,
                subcategories = listOf(
                    Category(
                        name = "Tractor",
                        route = "",
                        icon = R.drawable.baseline_agriculture_24,
                        specifications = listOf(
                            CategorySpecification(
                                specification = Pair("Power", "power"),
                                amount = "50",
                                units = listOf("HP","KW")
                            ),
                            CategorySpecification(
                                specification = Pair("Model", "model"),
                                amount = "2020"
                            )
                        )
                    ),
                    Category(
                        name = "Rotavator",
                        route = "",
                        icon = R.drawable.baseline_agriculture_24
                    ),
                    Category(
                        name = "Plough",
                        route = "",
                        icon = R.drawable.baseline_agriculture_24
                    ),
                )
            ),
            Category(
                name = "Harvesting",
                route = "",
                icon = R.drawable.baseline_agriculture_24
            ),
            Category(
                name = "Electrical",
                route = "",
                icon = R.drawable.baseline_agriculture_24
            ),
            Category(
                name = "Irrigation",
                route = "",
                icon = R.drawable.baseline_agriculture_24
            ),
            Category(
                name = "Other Equipment",
                route = "",
                icon = R.drawable.baseline_agriculture_24
            )
        )
    ),
    Category(
        name = "New Items",
        route = "",
        icon = R.drawable.baseline_agriculture_24,
        subcategories = listOf(
            Category(name = "Cultivation", route = "", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Harvesting", route = "", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Electrical", route = "", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Irrigation", route = "", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Other Equipment", route = "", icon = R.drawable.baseline_agriculture_24)
        )
    ),
    Category(
        name = "Land",
        route = "",
        icon = R.drawable.baseline_handshake_24,
        subcategories = listOf(
            Category(name = "Sell", route = "", icon = R.drawable.baseline_handshake_24),
            Category(name = "Fixed rent", route = "", icon = R.drawable.baseline_handshake_24),
            Category(name = "Partnership farming", route = "", icon = R.drawable.baseline_handshake_24)
        )
    ),
    Category(
        name = "Service",
        route = "",
        icon = R.drawable.baseline_handshake_24,
        subcategories = listOf(
            Category(name = "Cultivation", route = "", icon = R.drawable.baseline_handshake_24),
            Category(name = "Harvesting", route = "", icon = R.drawable.baseline_handshake_24),
            Category(name = "JCB", route = "", icon = R.drawable.baseline_handshake_24),
            Category(name = "Farm pond maker", route = "", icon = R.drawable.baseline_handshake_24),
        )
    )

)

fun getCategoryInfo(synonym:String): Triple<Category?, Category?, Category?> {
    var category:Category? = null
    var parentCategory:Category? = null
    var grandParentCategory:Category? = null
    for (parent in productCategories){
        for (child in parent.subcategories){
            if (child.name.equals(synonym,true)){
                category = child
                parentCategory = parent
                grandParentCategory = productCategories.find { it.subcategories.contains(parent) }
                break
            }
        }
    }
    return Triple(grandParentCategory,parentCategory,category)
}