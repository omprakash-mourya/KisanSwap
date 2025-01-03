package com.kisanswap.kisanswap.user.buyer.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kisanswap.kisanswap.user.buyer.model.User
import com.kisanswap.kisanswap.user.buyer.repository.BuyerUserFirebaseRepository
import com.kisanswap.kisanswap.user.buyer.viewModel.BuyerViewModel
import com.kisanswap.kisanswap.user.buyer.viewModel.viewmodelfactory.BuyerViewModelFactory

@Composable
fun UserProfileScreen(firebaseRepository: BuyerUserFirebaseRepository) {
    val buyerViewModel: BuyerViewModel = viewModel(factory = BuyerViewModelFactory(firebaseRepository))
    val context = LocalContext.current
    val user = remember { mutableStateOf(User()) }

    Column {
        OutlinedTextField(
            value = user.value.name,
            onValueChange = { user.value = user.value.copy(name = it) },
            label = { Text("Name") }
        )
        OutlinedTextField(
            value = user.value.email,
            onValueChange = { user.value = user.value.copy(email = it) },
            label = { Text("Email") }
        )
        // Add more fields as needed

        Button(onClick = {
            buyerViewModel.createUser(user.value) { success ->
                if (success) {
                    Toast.makeText(context, "User created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to create user", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Create User")
        }

        Button(onClick = {
            buyerViewModel.updateUser(user.value) { success ->
                if (success) {
                    Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Update User")
        }
    }
}