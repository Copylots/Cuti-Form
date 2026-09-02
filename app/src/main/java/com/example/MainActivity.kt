package com.example

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.theme.MyApplicationTheme
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    var currentScreen by remember { mutableStateOf("login") }
    
    // Form Inputs State
    var namaPt by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var mulaiBekerja by remember { mutableStateOf("") }
    var keperluan by remember { mutableStateOf("") }
    var cutiDari by remember { mutableStateOf("") }
    var masukKembali by remember { mutableStateOf("") }
    var jabatan by remember { mutableStateOf("") }
    var wilayah by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }

    // Authorization Names & Titles
    var ttdPemohon by remember { mutableStateOf("") }
    var jabPemohon by remember { mutableStateOf("") }
    var ttdMengetahui by remember { mutableStateOf("") }
    var jabMengetahui by remember { mutableStateOf("") }
    var ttdMengetahui2 by remember { mutableStateOf("") }
    var jabMengetahui2 by remember { mutableStateOf("") }

    // Signature Canvas Strokes
    val padPemohonStrokes = remember { mutableStateListOf<List<Offset>>() }
    val padMengetahui1Strokes = remember { mutableStateListOf<List<Offset>>() }
    val padMengetahui2Strokes = remember { mutableStateListOf<List<Offset>>() }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            "login" -> LoginScreen(
                onLoginSuccess = { currentScreen = "main" }
            )
            "main" -> MainScreen(
                namaPt = namaPt, onNamaPtChange = { namaPt = it },
                nik = nik, onNikChange = { nik = it },
                nama = nama, onNamaChange = { nama = it },
                dept = dept, onDeptChange = { dept = it },
                mulaiBekerja = mulaiBekerja, onMulaiBekerjaChange = { mulaiBekerja = it },
                keperluan = keperluan, onKeperluanChange = { keperluan = it },
                cutiDari = cutiDari, onCutiDariChange = { cutiDari = it },
                masukKembali = masukKembali, onMasukKembaliChange = { masukKembali = it },
                jabatan = jabatan, onJabatanChange = { jabatan = it },
                wilayah = wilayah, onWilayahChange = { wilayah = it },
                lokasi = lokasi, onLokasiChange = { lokasi = it },
                
                ttdPemohon = ttdPemohon, onTtdPemohonChange = { ttdPemohon = it },
                jabPemohon = jabPemohon, onJabPemohonChange = { jabPemohon = it },
                ttdMengetahui = ttdMengetahui, onTtdMengetahuiChange = { ttdMengetahui = it },
                jabMengetahui = jabMengetahui, onJabMengetahuiChange = { jabMengetahui = it },
                ttdMengetahui2 = ttdMengetahui2, onTtdMengetahui2Change = { ttdMengetahui2 = it },
                jabMengetahui2 = jabMengetahui2, onJabMengetahui2Change = { jabMengetahui2 = it },
                
                padPemohonStrokes = padPemohonStrokes,
                padMengetahui1Strokes = padMengetahui1Strokes,
                padMengetahui2Strokes = padMengetahui2Strokes,
                
                onLogout = { currentScreen = "login" }
            )
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 36.dp)
    ) {
        // Hero Header Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_leave_header_1788388916690),
                contentDescription = "Header Cuti Karyawan",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FORM LOGIN",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("login_title")
            )

            Text(
                text = "Silakan masuk untuk mengajukan atau menyetujui form cuti",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // Username input
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = false
                },
                label = { Text("Username") },
                placeholder = { Text("Masukkan username Anda") },
                isError = usernameError,
                supportingText = {
                    if (usernameError) {
                        Text("Masukkan username Anda", color = MaterialTheme.colorScheme.error)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("username_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = { Text("Password") },
                placeholder = { Text("Masukkan password Anda") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError,
                supportingText = {
                    if (passwordError) {
                        Text("Masukkan password Anda", color = MaterialTheme.colorScheme.error)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("password_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val trimmedUser = username.trim()
                    val trimmedPass = password.trim()
                    
                    if (trimmedUser == "admin" && trimmedPass == "admin234") {
                        Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    } else {
                        if (trimmedUser != "admin") {
                            usernameError = true
                        }
                        if (trimmedPass != "admin234") {
                            passwordError = true
                        }
                        Toast.makeText(context, "Username atau Password Salah!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "MASUK",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    namaPt: String, onNamaPtChange: (String) -> Unit,
    nik: String, onNikChange: (String) -> Unit,
    nama: String, onNamaChange: (String) -> Unit,
    dept: String, onDeptChange: (String) -> Unit,
    mulaiBekerja: String, onMulaiBekerjaChange: (String) -> Unit,
    keperluan: String, onKeperluanChange: (String) -> Unit,
    cutiDari: String, onCutiDariChange: (String) -> Unit,
    masukKembali: String, onMasukKembaliChange: (String) -> Unit,
    jabatan: String, onJabatanChange: (String) -> Unit,
    wilayah: String, onWilayahChange: (String) -> Unit,
    lokasi: String, onLokasiChange: (String) -> Unit,
    
    ttdPemohon: String, onTtdPemohonChange: (String) -> Unit,
    jabPemohon: String, onJabPemohonChange: (String) -> Unit,
    ttdMengetahui: String, onTtdMengetahuiChange: (String) -> Unit,
    jabMengetahui: String, onJabMengetahuiChange: (String) -> Unit,
    ttdMengetahui2: String, onTtdMengetahui2Change: (String) -> Unit,
    jabMengetahui2: String, onJabMengetahui2Change: (String) -> Unit,
    
    padPemohonStrokes: SnapshotStateList<List<Offset>>,
    padMengetahui1Strokes: SnapshotStateList<List<Offset>>,
    padMengetahui2Strokes: SnapshotStateList<List<Offset>>,
    
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var activeDatePickerField by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FORM CUTI KARYAWAN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // DATA KARYAWAN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "DATA KARYAWAN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = namaPt,
                        onValueChange = onNamaPtChange,
                        label = { Text("Nama Perusahaan / PT") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nik,
                        onValueChange = onNikChange,
                        label = { Text("No Induk Karyawan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nama,
                        onValueChange = onNamaChange,
                        label = { Text("Nama Karyawan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dept,
                        onValueChange = onDeptChange,
                        label = { Text("Departemen") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Mulai Bekerja Date
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = mulaiBekerja,
                            onValueChange = {},
                            label = { Text("Mulai Bekerja") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { activeDatePickerField = "mulai_bekerja" }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = keperluan,
                        onValueChange = onKeperluanChange,
                        label = { Text("Untuk Keperluan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Cuti Dari Date
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = cutiDari,
                            onValueChange = {},
                            label = { Text("Cuti Diajukan Dari Tanggal") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { activeDatePickerField = "cuti_dari" }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Masuk Kembali Date
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = masukKembali,
                            onValueChange = {},
                            label = { Text("Masuk Kembali Tanggal") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { activeDatePickerField = "masuk_kembali" }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = jabatan,
                        onValueChange = onJabatanChange,
                        label = { Text("Jabatan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = wilayah,
                        onValueChange = onWilayahChange,
                        label = { Text("Wilayah") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = lokasi,
                        onValueChange = onLokasiChange,
                        label = { Text("Lokasi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // OTORISASI & SIGNATURE CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "OTORISASI & SIGNATURE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = ttdPemohon,
                        onValueChange = onTtdPemohonChange,
                        label = { Text("Nama Yang Mengajukan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = jabPemohon,
                        onValueChange = onJabPemohonChange,
                        label = { Text("Jabatan Yang Mengajukan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    OutlinedTextField(
                        value = ttdMengetahui,
                        onValueChange = onTtdMengetahuiChange,
                        label = { Text("Nama Mengetahui 1") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = jabMengetahui,
                        onValueChange = onJabMengetahuiChange,
                        label = { Text("Jabatan Mengetahui 1") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    OutlinedTextField(
                        value = ttdMengetahui2,
                        onValueChange = onTtdMengetahui2Change,
                        label = { Text("Nama Mengetahui 2") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = jabMengetahui2,
                        onValueChange = onJabMengetahui2Change,
                        label = { Text("Jabatan Mengetahui 2") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // GORESKAN TANDA TANGAN CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "GORESKAN TANDA TANGAN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Signature Pad Pemohon
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Mengajukan",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        SignaturePad(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            strokes = padPemohonStrokes
                        )
                    }

                    // Signature Pad Mengetahui 1
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Mengetahui 1",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        SignaturePad(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            strokes = padMengetahui1Strokes
                        )
                    }

                    // Signature Pad Mengetahui 2
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Mengetahui 2",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        SignaturePad(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            strokes = padMengetahui2Strokes
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            padPemohonStrokes.clear()
                            padMengetahui1Strokes.clear()
                            padMengetahui2Strokes.clear()
                            Toast.makeText(context, "Tanda tangan dihapus!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                    ) {
                        Text("HAPUS SEMUA TTD")
                    }

                    Button(
                        onClick = {
                            generateAndSharePdf(
                                context = context,
                                namaPt = namaPt,
                                nik = nik,
                                nama = nama,
                                dept = dept,
                                mulaiBekerja = mulaiBekerja,
                                keperluan = keperluan,
                                cutiDari = cutiDari,
                                masukKembali = masukKembali,
                                jabatan = jabatan,
                                wilayah = wilayah,
                                lokasi = lokasi,
                                ttdPemohon = ttdPemohon,
                                jabPemohon = jabPemohon,
                                ttdMengetahui = ttdMengetahui,
                                jabMengetahui = jabMengetahui,
                                ttdMengetahui2 = ttdMengetahui2,
                                jabMengetahui2 = jabMengetahui2,
                                signaturePemohon = padPemohonStrokes,
                                signatureMengetahui1 = padMengetahui1Strokes,
                                signatureMengetahui2 = padMengetahui2Strokes
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("CETAK FORMULIR KE PDF")
                    }
                }
            }
        }
    }

    // Active Date Picker dialog
    if (activeDatePickerField != null) {
        AppDatePickerDialog(
            onDismissRequest = { activeDatePickerField = null },
            onDateSelected = { dateStr ->
                when (activeDatePickerField) {
                    "mulai_bekerja" -> onMulaiBekerjaChange(dateStr)
                    "cuti_dari" -> onCutiDariChange(dateStr)
                    "masuk_kembali" -> onMasukKembaliChange(dateStr)
                }
            }
        )
    }
}

@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    strokes: MutableList<List<Offset>>
) {
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Box(
        modifier = modifier
            .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentStroke = listOf(offset)
                    },
                    onDragEnd = {
                        if (currentStroke.isNotEmpty()) {
                            strokes.add(currentStroke)
                            currentStroke = emptyList()
                        }
                    },
                    onDragCancel = {
                        currentStroke = emptyList()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentStroke = currentStroke + change.position
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw completed strokes
            strokes.forEach { stroke ->
                if (stroke.size > 1) {
                    val path = Path().apply {
                        val first = stroke.first()
                        moveTo(first.x, first.y)
                        for (i in 1 until stroke.size) {
                            val p = stroke[i]
                            lineTo(p.x, p.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                } else if (stroke.isNotEmpty()) {
                    drawCircle(
                        color = Color.Black,
                        radius = 1.5.dp.toPx(),
                        center = stroke.first()
                    )
                }
            }

            // Draw current active stroke
            if (currentStroke.size > 1) {
                val path = Path().apply {
                    val first = currentStroke.first()
                    moveTo(first.x, first.y)
                    for (i in 1 until currentStroke.size) {
                        val p = currentStroke[i]
                        lineTo(p.x, p.y)
                    }
                }
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            } else if (currentStroke.isNotEmpty()) {
                drawCircle(
                    color = Color.Black,
                    radius = 1.5.dp.toPx(),
                    center = currentStroke.first()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                if (selectedMillis != null) {
                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formatted = sdf.format(Date(selectedMillis))
                    onDateSelected(formatted)
                }
                onDismissRequest()
            }) {
                Text("PILIH")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("BATAL")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// PDF Drawing & Sharing Function
fun generateAndSharePdf(
    context: Context,
    namaPt: String,
    nik: String,
    nama: String,
    dept: String,
    mulaiBekerja: String,
    keperluan: String,
    cutiDari: String,
    masukKembali: String,
    jabatan: String,
    wilayah: String,
    lokasi: String,
    ttdPemohon: String,
    jabPemohon: String,
    ttdMengetahui: String,
    jabMengetahui: String,
    ttdMengetahui2: String,
    jabMengetahui2: String,
    signaturePemohon: List<List<Offset>>,
    signatureMengetahui1: List<List<Offset>>,
    signatureMengetahui2: List<List<Offset>>
) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Paints for drawing
        val borderPaint = Paint().apply {
            color = AndroidColor.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val textPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 9.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        val boldPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 9.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val headerPtPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 10f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val titlePaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 14f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val italicPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 8.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
        }

        val alignRightItalicPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 8.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.RIGHT
        }

        // 1. Draw outer page rectangle
        canvas.drawRect(14f, 14f, 581f, 828f, borderPaint)

        // 2. Draw Company Header (Nama PT)
        val ptText = if (namaPt.isNotBlank()) namaPt.uppercase() else "NAMA PERUSAHAAN / PT"
        canvas.drawText(ptText, 30f, 40f, headerPtPaint)

        // 3. Draw Main Title
        canvas.drawText("FORM PENGAJUAN CUTI KARYAWAN", 297.5f, 75f, titlePaint)

        // 4. Separator line under title
        canvas.drawLine(30f, 95f, 565f, 95f, borderPaint)

        // 5. Helper function for drawing structured row item
        fun drawRowItem(label: String, valText: String, x: Float, y: Float) {
            canvas.drawText(label, x, y, textPaint)
            canvas.drawText(":", x + 105f, y, textPaint)
            canvas.drawText(valText, x + 115f, y, boldPaint)
        }

        // Draw Data Karyawan
        val dataYStart = 120f
        val spacing = 18f

        drawRowItem("No Induk Karyawan", nik, 30f, dataYStart)
        drawRowItem("Jabatan", jabatan, 310f, dataYStart)

        drawRowItem("Nama Karyawan", nama, 30f, dataYStart + spacing)
        drawRowItem("Wilayah", wilayah, 310f, dataYStart + spacing)

        drawRowItem("Departemen", dept, 30f, dataYStart + spacing * 2)
        drawRowItem("Lokasi", lokasi, 310f, dataYStart + spacing * 2)

        drawRowItem("Mulai Bekerja", mulaiBekerja, 30f, dataYStart + spacing * 3)
        drawRowItem("Keperluan", keperluan, 310f, dataYStart + spacing * 3)

        drawRowItem("Cuti Diajukan", cutiDari, 30f, dataYStart + spacing * 4)
        drawRowItem("Masuk Kembali", masukKembali, 310f, dataYStart + spacing * 4)

        // Separator line
        val pmtYStart = dataYStart + spacing * 5 + 10f
        canvas.drawLine(30f, pmtYStart, 565f, pmtYStart, borderPaint)

        // Section Title: Pemotongan
        canvas.drawText("Pemotongan :", 30f, pmtYStart + 20f, headerPtPaint)

        // Draw Leave cutting table values
        val tableYStart = pmtYStart + 40f
        val tableSpacing = 16f

        drawRowItem("- Sakit Tanpa Surat Dokter", "........................ Hari", 30f, tableYStart)
        drawRowItem("- Hak Cuti Tahunan", "............ : ............ Hari", 310f, tableYStart)

        drawRowItem("- Ijin Keperluan Lain", "........................ Hari", 30f, tableYStart + tableSpacing)
        drawRowItem("- Sudah Digunakan", "............ : ............ Hari", 310f, tableYStart + tableSpacing)

        drawRowItem("- Mangkir", "........................ Hari", 30f, tableYStart + tableSpacing * 2)
        drawRowItem("- Sisa Cuti Tahunan", "............ : ............ Hari", 310f, tableYStart + tableSpacing * 2)

        // Row with "=" instead of ":"
        canvas.drawText("- Jumlah Pemotongan", 30f, tableYStart + tableSpacing * 3, textPaint)
        canvas.drawText("=", 30f + 105f, tableYStart + tableSpacing * 3, textPaint)
        canvas.drawText("........................ Hari", 30f + 115f, tableYStart + tableSpacing * 3, boldPaint)
        
        drawRowItem("- Cuti Yang Diajukan", "............ : ............ Hari", 310f, tableYStart + tableSpacing * 3)
        drawRowItem("- Sisa Cuti Tahunan", "............ : ............ Hari", 310f, tableYStart + tableSpacing * 4)

        // Catatan
        val noteY = tableYStart + tableSpacing * 5 + 15f
        canvas.drawText("Catatan : .........................................................................................................................................", 30f, noteY, textPaint)

        // Printed Time
        val printTimeY = noteY + 25f
        val formattedTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Dicetak pada tanggal : $formattedTime", 565f, printTimeY, alignRightItalicPaint)

        // Separator line
        val signYStart = printTimeY + 12f
        canvas.drawLine(30f, signYStart, 565f, signYStart, borderPaint)

        // Headers for Signatures
        val colHeaderPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 9.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        val headerY = signYStart + 22f
        canvas.drawText("Yang Mengajukan,", 120f, headerY, colHeaderPaint)
        canvas.drawText("Mengetahui,", 297.5f, headerY, colHeaderPaint)
        canvas.drawText("Mengetahui,", 475f, headerY, colHeaderPaint)

        // Draw signatures inside vector boxes
        val boxY = headerY + 10f
        val boxW = 90f
        val boxH = 50f

        drawSignatureOnPdf(canvas, signaturePemohon, 120f - boxW / 2f, boxY, boxW, boxH)
        drawSignatureOnPdf(canvas, signatureMengetahui1, 297.5f - boxW / 2f, boxY, boxW, boxH)
        drawSignatureOnPdf(canvas, signatureMengetahui2, 475f - boxW / 2f, boxY, boxW, boxH)

        // Names & Positions below signatures
        val signNamePaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 9.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val signPosPaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 8.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            textAlign = Paint.Align.CENTER
        }

        val nameY = boxY + boxH + 20f
        val posY = nameY + 12f

        val np = if (ttdPemohon.isNotBlank()) ttdPemohon else "....................................."
        val jp = if (jabPemohon.isNotBlank()) jabPemohon else "....................................."
        canvas.drawText("( $np )", 120f, nameY, signNamePaint)
        canvas.drawText(jp, 120f, posY, signPosPaint)

        val nm1 = if (ttdMengetahui.isNotBlank()) ttdMengetahui else "....................................."
        val jm1 = if (jabMengetahui.isNotBlank()) jabMengetahui else "....................................."
        canvas.drawText("( $nm1 )", 297.5f, nameY, signNamePaint)
        canvas.drawText(jm1, 297.5f, posY, signPosPaint)

        val nm2 = if (ttdMengetahui2.isNotBlank()) ttdMengetahui2 else "....................................."
        val jm2 = if (jabMengetahui2.isNotBlank()) jabMengetahui2 else "....................................."
        canvas.drawText("( $nm2 )", 475f, nameY, signNamePaint)
        canvas.drawText(jm2, 475f, posY, signPosPaint)

        // Finish pdf page
        pdfDocument.finishPage(page)

        // Save PDF to cache directory first for sharing
        val dateStamp = SimpleDateFormat("dd-MM-yyyy_HHmm", Locale.getDefault()).format(Date())
        val nameClean = if (nama.isNotBlank()) nama.replace("\\s+".toRegex(), "") else "Karyawan"
        val filename = "Cuti_${nameClean}_$dateStamp.pdf"
        val cacheFile = File(context.cacheDir, filename)

        val stream = ByteArrayOutputStream()
        pdfDocument.writeTo(stream)
        pdfDocument.close()
        
        val pdfBytes = stream.toByteArray()
        FileOutputStream(cacheFile).use { out ->
            out.write(pdfBytes)
        }

        // Save to public Downloads directory
        try {
            savePdfToDownloads(context, pdfBytes, filename)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Trigger Share sheet using FileProvider
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, cacheFile)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Lihat Dokumen atau Kirim via:")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)

        Toast.makeText(context, "Sukses! PDF disalin ke folder Download", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Gagal menyusun dokumen PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

// Draw signature strokes centered and scaled within bounds
fun drawSignatureOnPdf(
    canvas: android.graphics.Canvas,
    strokes: List<List<Offset>>,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) {
    if (strokes.isEmpty()) return

    var minX = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var minY = Float.MAX_VALUE
    var maxY = Float.MIN_VALUE

    strokes.forEach { stroke ->
        stroke.forEach { pt ->
            if (pt.x < minX) minX = pt.x
            if (pt.x > maxX) maxX = pt.x
            if (pt.y < minY) minY = pt.y
            if (pt.y > maxY) maxY = pt.y
        }
    }

    val totalStrokeWidth = maxX - minX
    val totalStrokeHeight = maxY - minY

    if (totalStrokeWidth <= 0f || totalStrokeHeight <= 0f) return

    // Scale to fit 85% of target box width/height to have padding margins
    val scale = minOf(width / totalStrokeWidth, height / totalStrokeHeight) * 0.85f

    val targetCenterX = x + width / 2f
    val targetCenterY = y + height / 2f
    val strokeCenterX = minX + totalStrokeWidth / 2f
    val strokeCenterY = minY + totalStrokeHeight / 2f

    val paint = Paint().apply {
        color = AndroidColor.BLACK
        style = Paint.Style.STROKE
        this.strokeWidth = 1.8f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    strokes.forEach { stroke ->
        if (stroke.isEmpty()) return@forEach
        val path = android.graphics.Path()

        val first = stroke.first()
        val fx = targetCenterX + (first.x - strokeCenterX) * scale
        val fy = targetCenterY + (first.y - strokeCenterY) * scale
        path.moveTo(fx, fy)

        for (i in 1 until stroke.size) {
            val pt = stroke[i]
            val px = targetCenterX + (pt.x - strokeCenterX) * scale
            val py = targetCenterY + (pt.y - strokeCenterY) * scale
            path.lineTo(px, py)
        }
        canvas.drawPath(path, paint)
    }
}

// MediaStore helper to save file directly to public Downloads folder
fun savePdfToDownloads(context: Context, pdfBytes: ByteArray, filename: String): Uri? {
    val resolver = context.contentResolver
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { out ->
                out.write(pdfBytes)
            }
        }
        uri
    } else {
        @Suppress("DEPRECATION")
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, filename)
        file.outputStream().use { out ->
            out.write(pdfBytes)
        }
        Uri.fromFile(file)
    }
}
