package com.kisanswap.kisanswap.common.components

import android.app.Activity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import com.kisanswap.kisanswap.PreferenceManager
import com.kisanswap.kisanswap.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val activity = context as Activity
    val density = LocalDensity.current
    val preferenceManager = PreferenceManager(context)
    val navigationBarHeight = with(density) {
        androidx.core.view.ViewCompat.getRootWindowInsets(activity.window.decorView)
            ?.getInsets(androidx.core.view.WindowInsetsCompat.Type.navigationBars())?.bottom?.toDp() ?: 0.dp
    }
    val items = listOf(
        BottomNavItem("Home", R.drawable.baseline_home_24, "home-page"),
        BottomNavItem("Sell", R.drawable.baseline_sell_24, "sell"),
        BottomNavItem("My Product", R.drawable.baseline_compost_24, "my-products"),
        BottomNavItem("Account", R.drawable.baseline_account_circle_24, "account")
    )
    val coroutineScope = rememberCoroutineScope()

    preferenceManager.setNavigationBarPadding(navigationBarHeight.value.toInt())

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFAEFFB0),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp)
            )
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp)
            ),
        containerColor = Color(0xFFAAFFAE),
        contentColor = Color.Black,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected by remember { mutableStateOf(navController.currentDestination?.route == item.route) }
            NavigationBarItem(
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
//                    isSelected = navController.currentDestination?.route == item.route
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (isSelected) Color.Green else Color.Black,
                        modifier = Modifier.size(
                            if (isSelected) 36.dp else 30.dp
                        )
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Color(0xFF075D00) else Color.Black,
                        style = MaterialTheme.typography.labelLarge
                    )
                        },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00560F),
                    unselectedIconColor = Color.Black,
                    selectedTextColor = Color(0xFF076900),
                    unselectedTextColor = Color.Black,
                    indicatorColor = Color(0xFFFFFFFF)
                )
            )
        }
    }

    /*Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                AnimatedIcon(
                    item = item,
                    isSelected = selectedIndex == index,
                    onClick = {
                        selectedIndex = index
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }*/
}

@Composable
fun AnimatedIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val transition = updateTransition(targetState = isSelected, label = "IconTransition")
    val offsetY by transition.animateDp(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "OffsetY"
    ) { if (it) (-16).dp else 0.dp }

    val scale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "Scale"
    ) { if (it) 1.2f else 1f }

    Box(
        modifier = Modifier
            .size(56.dp)
            .offset(y = offsetY)
            .clickable(onClick = onClick)
            .clip(CircleShape)
            .background(if (isSelected) Color.White else Color.Transparent)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = item.icon),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp).scale(scale),
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = item.label,
        style = MaterialTheme.typography.labelSmall,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    )
}

data class BottomNavItem(val label: String,
                         val icon: Int,
                         val route: String)

/*
@Composable
fun RowScope.BottomNavItemComposable(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium)
) {
    val styledLabel: @Composable (() -> Unit)? = label?.let {
        @Composable {
            val style = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.Center)
            ProvideTextStyle(style, content = label)
        }
    }
    val ripple = rememberRipple(bounded = false, color = selectedContentColor)

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = ripple
            )
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        BottomNavTransition(
            selectedContentColor,
            unselectedContentColor,
            selected
        ) { progress ->
            val animationProgress = if (alwaysShowLabel) 1f else progress

            BottomNavItemBaselineLayout(
                icon = icon,
                label = styledLabel,
                iconPositionAnimationProgress = animationProgress
            )
        }
    }
}

@Composable
private fun BottomNavTransition(
    activeColor: Color,
    inactiveColor: Color,
    selected: Boolean,
    content: @Composable (animationProgress: Float) -> Unit
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = BottomNavAnimationSpec
    )

    val color = lerp(inactiveColor, activeColor, animationProgress)

    CompositionLocalProvider(
        LocalContentColor provides color.copy(alpha = 1f),
        LocalContentAlpha provides color.alpha,
    ) {
        content(animationProgress)
    }
}

@Composable
private fun BottomNavItemBaselineLayout(
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    iconPositionAnimationProgress: Float
) {
    Layout(
        {
            Box(Modifier.layoutId("icon")) { icon() }
            if (label != null) {
                Box(
                    Modifier
                        .layoutId("label")
                        .alpha(iconPositionAnimationProgress)
                        .padding(horizontal = BottomNavItemHorizontalPadding)
                ) { label() }
            }
        }
    ) { measurables, constraints ->
        val iconPlaceable = measurables.fastFirst { it.layoutId == "icon" }.measure(constraints)

        val labelPlaceable = label?.let {
            measurables.fastFirst { it.layoutId == "label" }.measure(
                constraints.copy(minHeight = 0)
            )
        }

        if (label == null) {
            placeIcon(iconPlaceable, constraints)
        } else {
            placeLabelAndIcon(
                labelPlaceable!!,
                iconPlaceable,
                constraints,
                iconPositionAnimationProgress
            )
        }
    }
}

private fun MeasureScope.placeIcon(
    iconPlaceable: Placeable,
    constraints: Constraints
): MeasureResult {
    val height = constraints.constrainHeight(BottomNavHeight.roundToPx())
    val iconY = (height - iconPlaceable.height) / 2
    return layout(iconPlaceable.width, height) {
        iconPlaceable.placeRelative(0, iconY)
    }
}

private fun MeasureScope.placeLabelAndIcon(
    labelPlaceable: Placeable,
    iconPlaceable: Placeable,
    constraints: Constraints,
    iconPositionAnimationProgress: Float
): MeasureResult {
    val firstBaseline = labelPlaceable[FirstBaseline]
    val baselineOffset = CombinedItemTextBaseline.roundToPx()
    val netBaselineAdjustment = baselineOffset - firstBaseline

    val contentHeight = iconPlaceable.height + labelPlaceable.height + netBaselineAdjustment
    val height = constraints.constrainHeight(max(contentHeight, BottomNavHeight.roundToPx()))
    val contentVerticalPadding = ((height - contentHeight) / 2).coerceAtLeast(0)

    val unselectedIconY = (height - iconPlaceable.height) / 2
    val selectedIconY = contentVerticalPadding

    val labelY = selectedIconY + iconPlaceable.height + netBaselineAdjustment

    val containerWidth = max(labelPlaceable.width, iconPlaceable.width)

    val labelX = (containerWidth - labelPlaceable.width) / 2
    val iconX = (containerWidth - iconPlaceable.width) / 2

    val iconDistance = unselectedIconY - selectedIconY

    val offset = (iconDistance * (1 - iconPositionAnimationProgress)).roundToInt()

    return layout(containerWidth, height) {
        if (iconPositionAnimationProgress != 0f) {
            labelPlaceable.placeRelative(labelX, labelY + offset)
        }
        iconPlaceable.placeRelative(iconX, selectedIconY + offset)
    }
}

private val BottomNavAnimationSpec = TweenSpec<Float>(
    durationMillis = 300,
    easing = FastOutSlowInEasing
)

private val BottomNavHeight = 56.dp
private val BottomNavItemHorizontalPadding = 12.dp
private val CombinedItemTextBaseline = 12.dp

*/