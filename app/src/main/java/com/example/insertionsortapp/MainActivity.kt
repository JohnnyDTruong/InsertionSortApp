package com.example.insertionsortapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun InsertionSortApp() {
    var inputText by remember { mutableStateOf("") }
    var sortingSteps by remember { mutableStateOf<List<List<Pair<Int, Boolean>>>>(emptyList()) }
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
                Text(text = "Enter Numbers (0-9) To Sort:")

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

                sortingSteps.forEachIndexed { stepIndex, step ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = "Step ${stepIndex + 1}: ")
                        step.forEach { (number, isSorted) ->
                            Text(
                                text = "$number ",
                                color = if (isSorted) Color.Green else Color.Red
                            )
                        }
                    }
                }
            }
        }
    )
}

fun performSort(input: String, onResult: (List<List<Pair<Int, Boolean>>>, String?) -> Unit) {
    // Split numbers based on spaces or treat as continuous digits
    val numbers = if (input.contains(" ")) {
        input.split(" ").mapNotNull { it.toIntOrNull() } // Handle spaced input
    } else {
        input.mapNotNull { it.toString().toIntOrNull() } // Handle continuous input
    }

    // Validate input size range
    if (numbers.size !in 3..8) {
        onResult(emptyList(), "Error message: Input size must be between 3 and 8 numbers.")
        return
    }

    // Validate input range (only 0-9 allowed)
    if (numbers.any { it !in 0..9 }) {
        onResult(emptyList(), "Error message: All numbers must be between 0 and 9.")
        return
    }

    val steps = mutableListOf<List<Pair<Int, Boolean>>>()
    val array = numbers.toMutableList()

    // Initial step with all numbers unsorted
    steps.add(array.map { it to false })

    // Perform insertion sort and capture each step with color-coding
    // Red for Not Sorted Yet
    // Green for Sorted
    for (i in 1 until array.size) {
        val key = array[i]
        var j = i - 1

        // Shift elements that are greater than key
        while (j >= 0 && array[j] > key) {
            array[j + 1] = array[j]
            j--
        }
        array[j + 1] = key

        // Mark sorted elements up to index i
        steps.add(array.mapIndexed { index, value ->
            value to (index <= i)
        })
    }

    onResult(steps, null) // Return sorted steps with no error
}

@Preview(showBackground = true)
@Composable
fun InsertionSortAppPreview() {
    InsertionSortAppTheme {
        InsertionSortApp()
    }
}
