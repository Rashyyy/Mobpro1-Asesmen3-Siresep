package com.rasya0020.siresep.ui.theme.screen

import android.R.attr.enabled
import android.R.attr.type
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDialog(
    bitmap: Bitmap?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String, String, String) -> Unit
) {
    var judul by remember { mutableStateOf("") }
    var durasi by remember { mutableStateOf("") }
    var tingkatKesulitan by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Mudah", "Sedang", "Sulit")

    var deskripsi by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Gambar Resep",
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    )
                }

                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text(text = "Judul Resep") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = durasi,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            durasi = it
                        }
                    },
                    label = { Text(text = "Durasi (Menit)") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = tingkatKesulitan,
                        onValueChange = {},
                        label = { Text("Tingkat Kesulitan") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    tingkatKesulitan = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi/Cara Memasak") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Batal")
                    }
                    OutlinedButton(
                        onClick = { onConfirmation(judul, durasi, tingkatKesulitan, deskripsi) },
                        enabled = judul.isNotEmpty() && durasi.isNotEmpty() && tingkatKesulitan.isNotEmpty() && deskripsi.isNotEmpty(),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Simpan")
                    }
                }
            }
        }
    }
}