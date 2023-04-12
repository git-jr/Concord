package com.alura.concord.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun ConcordNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = chatListRoute,
//        startDestination = "messages/${6}",
        modifier = modifier,
    ) {

        chatListGraph(
            onOpenChat = { chatId ->
                navController.navigateToMessageScreen(chatId)
            }
        )

        messageGraph(
            onBack = {
                navController.navigateUp()
            }
        )
    }
}


