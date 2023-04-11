package com.alura.concord.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun ConcordNavHost2(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = chatListRoute,
        modifier = modifier,
    ) {
        chatListGraph(
            onOpenChat = { chatId ->
                navController.navigateToMessageScreen(chatId)
            }
        )

        messageGraphBottoms(
            onBack = {
                navController.navigateUp()
            }
        )
    }
}
