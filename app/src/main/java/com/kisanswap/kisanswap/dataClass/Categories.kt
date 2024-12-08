package com.kisanswap.kisanswap.dataClass

import com.kisanswap.kisanswap.R

data class Category(
    val name: String,
    val icon: Int,
    val subcategories: List<Category> = emptyList()
)

val categories = listOf(
    Category(
        name = "Farming Equipment",
        icon = R.drawable.baseline_agriculture_24,
        subcategories = listOf(
            Category(name = "Electrical Equipment", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Tractor-powered Machines", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Irrigation Systems", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Other Equipment", icon = R.drawable.baseline_agriculture_24)
        )
    ),
    Category(
        name = "Farm Partnership & Rental",
        icon = R.drawable.baseline_handshake_24,
        subcategories = listOf(
            Category(name = "Partnership Opportunities", icon = R.drawable.baseline_handshake_24),
            Category(name = "Equipment Rentals", icon = R.drawable.baseline_handshake_24)
        )
    ),
    Category(
        name = "New Equipment",
        icon = R.drawable.baseline_agriculture_24,
        subcategories = listOf(
            Category(name = "Tractor and Accessories", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Harvesting Equipment", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Irrigation Systems", icon = R.drawable.baseline_agriculture_24),
            Category(name = "Electrical Systems", icon = R.drawable.baseline_agriculture_24)
        )
    )
)