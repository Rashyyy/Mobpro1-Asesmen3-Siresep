@file:Suppress("DEPRECATION")
package com.rasya0020.siresep.ui.theme.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.rasya0020.siresep.BuildConfig
import com.rasya0020.siresep.R
import com.rasya0020.siresep.model.Recipe
import com.rasya0020.siresep.model.User
import com.rasya0020.siresep.network.ApiStatus
import com.rasya0020.siresep.network.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val OrangeWarm = Color(0xFFFF6B35)
private val AmberGold = Color(0xFFFFA726)
private val BrownDeep = Color(0xFF4E2B10)
private val CreamBg = Color(0xFFFFF8F2)
private val GreenFresh = Color(0xFF4CAF50)
private val RedHot = Color(0xFFE53935)
private val YellowMed = Color(0xFFFFC107)
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
    var showEditDialog by remember { mutableStateOf(false) }
    var resepYangDiedit by remember { mutableStateOf<Recipe?>(null) }

    Scaffold(
        containerColor = CreamBg,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OrangeWarm)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🍳 " + stringResource(id = R.string.app_name),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (user.email.isNotEmpty()) "Halo, ${user.email.substringBefore("@")}!" else "Temukan resep lezat",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(onClick = {
                        if (user.email.isEmpty()){
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showDialog = true
                        }
                    }) {
                        if (user.photoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = user.photoUrl,
                                contentDescription = "Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_account_circle_24),
                                    contentDescription = stringResource(R.string.profil),
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val options = CropImageContractOptions(
                        null, CropImageOptions(
                            imageSourceIncludeGallery = false,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                },
                containerColor = OrangeWarm,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.tambah_resep),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.tambah_resep),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    )
    { innerPadding ->
        ScreenContent(viewModel, user.email, onEditClick = { resep ->
            resepYangDiedit = resep
            showEditDialog = true
        },Modifier.padding(innerPadding))
        if (showDialog){
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }
        if (showResepDialog){
            RecipeDialog(
                bitmap = bitmap,
                onDismissRequest = { showResepDialog = false }) { judul, durasi, tingkatKesulitan, deskripsi ->
                viewModel.simpanResep(user.email, judul, durasi, tingkatKesulitan, deskripsi, bitmap!!)
                showResepDialog = false
            }
        }
        if (showEditDialog && resepYangDiedit != null) {
            RecipeDialog(
                initialData = resepYangDiedit,
                onDismissRequest = { showEditDialog = false },
                onConfirmation = { judul, durasi, tingkat, deskripsi ->
                    viewModel.updateResep(
                        id = resepYangDiedit!!.id.toString(),
                        judul = judul,
                        durasi = durasi,
                        tingkatKesulitan = tingkat,
                        deskripsi = deskripsi,
                        email = user.email
                    )
                    showEditDialog = false
                }
            )
        }
        if (pesanError != null){
            Toast.makeText(context, pesanError, Toast.LENGTH_LONG).show()
            viewModel.bersihkanPesan()
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, userId: String,onEditClick: (Recipe) -> Unit, modifier: Modifier = Modifier) {
    val daftarResep by viewModel.daftarResep
    val statusApi by viewModel.statusApi.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedResepId by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.ambilDaftarResep(userId)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "🗑️ Hapus Resep",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = { Text(text = "Apakah Anda yakin ingin menghapus resep ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hapusResep(userId, selectedResepId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedHot),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Hapus", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Batal")
                }
            }
        )
    }

    when (statusApi) {
        ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = OrangeWarm,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memuat resep...",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
        ApiStatus.SUCCESS -> {
            if (daftarResep.isEmpty()) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🍽️", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum ada resep",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = BrownDeep
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap tombol + untuk menambahkan resep pertamamu!",
                            fontSize = 13.sp,
                            color = Color(0xFF999999),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    modifier = modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daftarResep) { resep ->
                        ItemResep(
                            resep = resep,
                            currentUserId = userId,
                            onDeleteClick = {
                                selectedResepId = resep.id.toString()
                                showDeleteDialog = true
                            },
                            onEditClick = { onEditClick(resep) }
                        )
                    }
                }
            }
        }
        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "😕", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.error),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = BrownDeep
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.ambilDaftarResep(userId) },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeWarm),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.try_again),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ItemResep(resep: Recipe, currentUserId: String, onDeleteClick: () -> Unit, onEditClick: () -> Unit) {
    val difficultyColor = when (resep.tingkatKesulitan.lowercase()) {
        "mudah", "easy" -> GreenFresh
        "sedang", "medium" -> YellowMed
        "sulit", "hard", "susah" -> RedHot
        else -> OrangeWarm
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(resep.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = resep.judul,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = difficultyColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = resep.tingkatKesulitan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                }
                if (resep.isMine == "1" && currentUserId.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp),
                    ) {
                        ActionIconButton(R.drawable.baseline_edit_24, "Edit") { onEditClick() }
                        Spacer(modifier = Modifier.width(24.dp))
                        ActionIconButton(R.drawable.baseline_delete_24, "Hapus") { onDeleteClick() }
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = resep.judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = BrownDeep
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⏱", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${resep.durasi} menit",
                        fontSize = 11.sp,
                        color = OrangeWarm,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = resep.deskripsi,
                    fontSize = 11.sp,
                    color = Color(0xFF888888),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
            }
        }
    }
}


@Composable
fun ActionIconButton(icon: Int, desc: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(24.dp)
            .background(Color.Black.copy(alpha = 0.55f), shape = CircleShape)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            tint = Color.White,
            modifier = Modifier.size(15.dp)
        )
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore){
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
){
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException){
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    }
    else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore){
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful){
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}