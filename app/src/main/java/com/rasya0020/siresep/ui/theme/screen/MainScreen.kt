@file:Suppress("DEPRECATION")
package com.rasya0020.siresep.ui.theme.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.rasya0020.siresep.R
import com.rasya0020.siresep.model.Recipe
import com.rasya0020.siresep.model.User
import com.rasya0020.siresep.network.ApiStatus
import com.rasya0020.siresep.network.UserDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userflow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val pesanError by viewModel.pesanError

    var showDialog by remember { mutableStateOf(false) }
    var showResepDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()){
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showResepDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val options = CropImageContractOptions(null, CropImageOptions(
                    imageSourceIncludeGallery = false, imageSourceIncludeCamera = true, fixAspectRatio = true
                ))
                launcher.launch(options)
            }){ Icon(Icons.Default.Add, contentDescription = "Tambah Resep") }
        }
    ) { innerPadding ->
        ScreenContent(viewModel, Modifier.padding(innerPadding))

        if (showResepDialog){
            RecipeDialog(
                bitmap = bitmap,
                onDismissRequest = { showResepDialog = false }) { judul, durasi ->
                viewModel.simpanResep(judul, durasi, bitmap!!)
                showResepDialog = false
            }
        }
        if (pesanError != null){
            Toast.makeText(context, pesanError, Toast.LENGTH_LONG).show()
            viewModel.bersihkanPesan()
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val daftarResep by viewModel.daftarResep
    val statusApi by viewModel.statusApi.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.ambilDaftarResep()
    }

    when (statusApi) {
        ApiStatus.LOADING -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier.fillMaxSize().padding(4.dp),
                columns = GridCells.Fixed(2)
            ) {
                items(daftarResep) { resep ->
                    ItemResep(resep = resep)
                }
            }
        }
        ApiStatus.FAILED -> {  }
    }
}

@Composable
fun ItemResep(resep: Recipe) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(resep.tautanGambar)
                    .crossfade(true)
                    .build(),
                contentDescription = resep.judul,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = resep.judul, fontWeight = FontWeight.Bold)
                Text(text = "Durasi: ${resep.durasi}", fontSize = 12.sp)
                Text(text = "Tingkat: ${resep.tingkatKesulitan}", fontSize = 12.sp)
            }
        }
    }
}

private fun getCroppedImage(resolver: ContentResolver, result: CropImageView.CropResult): Bitmap? {
    if (!result.isSuccessful) return null
    val uri = result.uriContent ?: return null
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))
    }
}