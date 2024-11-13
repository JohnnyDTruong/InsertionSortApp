package com.example.insertionsortapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.insertionsortapp.ui.theme.InsertionSortAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InsertionSortAppTheme {
                InsertionSortApp()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertionSortApp() {
    var inputText by remember { mutableStateOf("") }
    var sortingSteps by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Enter numbers (0-9) separated by spaces:")

                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            performSort(inputText) { steps, error ->
                                sortingSteps = steps
                                errorMessage = error
                            }
                        }
                    ),
                    singleLine = true,
                    placeholder = { Text("e.g., 9 8 3 2 4 6 1") }
                )

                Button(
                    onClick = {
                        keyboardController?.hide()
                        performSort(inputText) { steps, error ->
                            sortingSteps = steps
                            errorMessage = error
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Sort")
                }

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Sorting Steps:")

                sortingSteps.forEach { step ->
                    Text(text = step)
                }
            }
        }
    )
}

fun performSort(input: String, onResult: (List<String>, String?) -> Unit) {
    // Modify parsing to handle both spaced and non-spaced numbers
    val numbers = if (input.contains(" ")) {
        input.split(" ").mapNotNull { it.toIntOrNull() }
    } else {
        input.mapNotNull { it.toString().toIntOrNull() }
    }

    // Validate input size
    if (numbers.size !in 3..8) {
        onResult(emptyList(), "Input size must be between 3 and 8 numbers.")
        return
    }

    // Validate input range
    if (numbers.any { it !in 0..9 }) {
        onResult(emptyList(), "All numbers must be between 0 and 9.")
        return
    }

    val steps = mutableListOf(numbers.joinToString(" "))
    val array = numbers.toMutableList()

    // Perform insertion sort and capture each step
    for (i in 1 until array.size) {
        val key = array[i]
        var j = i - 1

        // Shift elements of array[0..i-1], that are greater than key,
        // to one position ahead of their current position
        while (j >= 0 && array[j] > key) {
            array[j + 1] = array[j]
            j--
        }
        array[j + 1] = key
        steps.add(array.joinToString(" "))
    }

    onResult(steps, null)
}

@Preview(showBackground = true)
@Composable
fun InsertionSortAppPreview() {
    InsertionSortAppTheme {
        InsertionSortApp()
    }
}
