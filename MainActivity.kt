package ke.reberwet.jss.portal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

data class Learner(
    val admission: String,
    val name: String,
    val grade: String,
    val stream: String = "",
    val status: String = "Active"
)

data class Mark(
    val admission: String,
    val subject: String,
    val marks: Int,
    val rubric: String
)

object Rubric {
    val points = mapOf("EE1" to 8, "EE2" to 7, "ME1" to 6, "ME2" to 5,
        "AE1" to 4, "AE2" to 3, "BE1" to 2, "BE2" to 1)
    fun grade(m: Int) = when {
        m >= 70 -> "EE"
        m >= 45 -> "ME"
        m >= 15 -> "AE"
        else -> "BE"
    }
}

class MainActivity : ComponentActivity() {
    private val demoLearners = mutableStateListOf(
        Learner("DEMO001", "DEMO LEARNER 001", "Grade 9"),
        Learner("DEMO002", "DEMO LEARNER 002", "Grade 9"),
        Learner("DEMO003", "DEMO LEARNER 003", "Grade 8"),
        Learner("DEMO004", "DEMO LEARNER 004", "Grade 7")
    )

    private val subjects = listOf(
        "Mathematics","English","Kiswahili","Creative Arts & Sports",
        "Agriculture & Nutrition","Social Studies",
        "Christian Religious Education","Pre-Technical Studies","Integrated Science"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ReberwetApp() }
    }

    @Composable
    fun ReberwetApp() {
        var loggedIn by remember { mutableStateOf(false) }
        var role by remember { mutableStateOf("Administrator") }
        if (!loggedIn) {
            LoginScreen { selectedRole ->
                role = selectedRole
                loggedIn = true
            }
        } else {
            Portal(role) { loggedIn = false }
        }
    }

    @Composable
    fun LoginScreen(onLogin: (String) -> Unit) {
        var user by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var show by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf("") }

        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().padding(28.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("REBERWET JUNIOR SECONDARY SCHOOL", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Together we can make a difference.", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(28.dp))
                OutlinedTextField(user, { user = it }, Modifier.fillMaxWidth(), label = { Text("Username / email") })
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    password, { password = it }, Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = if (show) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = { TextButton({ show = !show }) { Text(if (show) "Hide" else "Show") } }
                )
                Spacer(Modifier.height(14.dp))
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
                Button(
                    onClick = {
                        if (user.isBlank() || password.isBlank()) error = "Enter username and password."
                        else onLogin(if (user.contains("admin", true)) "Administrator" else "Teacher")
                    },
                    Modifier.fillMaxWidth()
                ) { Text("LOGIN") }
                TextButton({ /* Firebase password reset is configured in AuthRepository */ }, Modifier.fillMaxWidth()) {
                    Text("Forgot password?")
                }
                OutlinedButton({ biometricLogin(onLogin) }, Modifier.fillMaxWidth()) {
                    Text("Use biometric")
                }
                Spacer(Modifier.height(12.dp))
                Text("DEMO/TEST MODE • No real learner data is included.", style = MaterialTheme.typography.labelSmall)
            }
        }
    }

    private fun biometricLogin(onLogin: (String) -> Unit) {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onLogin("Administrator")
            }
        })
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("ReberwetJS secure login")
                .setSubtitle("Authenticate with your device biometric")
                .setNegativeButtonText("Cancel")
                .build()
        )
    }

    @Composable
    fun Portal(role: String, logout: () -> Unit) {
        var page by remember { mutableStateOf("Dashboard") }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ReberwetJS") },
                    actions = { TextButton(logout) { Text("Logout") } }
                )
            },
            bottomBar = {
                NavigationBar {
                    listOf("Dashboard","Learners","Marks","Results","More").forEach {
                        NavigationBarItem(page == it, { page = it }, icon = {}, label = { Text(it) })
                    }
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad)) {
                when (page) {
                    "Dashboard" -> Dashboard(role)
                    "Learners" -> Learners()
                    "Marks" -> MarksEntry()
                    "Results" -> Results()
                    else -> More()
                }
            }
        }
    }

    @Composable
    fun Dashboard(role: String) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Role: $role")
                Spacer(Modifier.height(16.dp))
            }
            items(listOf(
                "Learners" to "${demoLearners.size}",
                "Teachers" to "9",
                "Classes" to "3",
                "Current term" to "Configure in Settings"
            )) { (a,b) ->
                Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                    Row(Modifier.padding(18.dp), Arrangement.SpaceBetween) {
                        Text(a, fontWeight = FontWeight.SemiBold); Text(b)
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Text("Class analytics", style = MaterialTheme.typography.titleLarge)
                Text("Grade distribution, subject performance and rubric distribution are available in Results.")
            }
        }
    }

    @Composable
    fun Learners() {
        var search by remember { mutableStateOf("") }
        var newName by remember { mutableStateOf("") }
        var newAdm by remember { mutableStateOf("") }
        var newGrade by remember { mutableStateOf("Grade 8") }
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Learner Management", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Search name / admission number") })
                Spacer(Modifier.height(10.dp))
                Text("ONBOARD NEW LEARNER", fontWeight = FontWeight.Bold)
                OutlinedTextField(newAdm, { newAdm = it }, Modifier.fillMaxWidth(), label = { Text("Admission number") })
                OutlinedTextField(newName, { newName = it }, Modifier.fillMaxWidth(), label = { Text("Full name") })
                OutlinedTextField(newGrade, { newGrade = it }, Modifier.fillMaxWidth(), label = { Text("Grade (e.g. Grade 8)") })
                Button({
                    if (newAdm.isNotBlank() && newName.isNotBlank()) {
                        demoLearners.add(Learner(newAdm, newName, newGrade))
                        newAdm = ""; newName = ""
                    }
                }) { Text("Add learner") }
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
            }
            items(demoLearners.filter {
                search.isBlank() || it.name.contains(search, true) || it.admission.contains(search, true)
            }) { l ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(l.name, fontWeight = FontWeight.Bold)
                        Text("${l.admission} • ${l.grade} • ${l.status}")
                    }
                }
            }
        }
    }

    @Composable
    fun MarksEntry() {
        var selected by remember { mutableStateOf(demoLearners.first()) }
        var subject by remember { mutableStateOf(subjects.first()) }
        var marksText by remember { mutableStateOf("") }
        var rubric by remember { mutableStateOf("EE1") }
        var message by remember { mutableStateOf("") }
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Marks Entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Learner: ${selected.name}")
            Text("Subject: $subject")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(marksText, { v ->
                if (v.all { it.isDigit() } && (v.isEmpty() || v.toInt() <= 100)) marksText = v
            }, Modifier.fillMaxWidth(), label = { Text("Marks (0–100)") })
            OutlinedTextField(rubric, { if (Rubric.points.containsKey(it.uppercase())) rubric = it.uppercase() },
                Modifier.fillMaxWidth(), label = { Text("Rubric level (EE1–BE2)") })
            Text("Rubric points: ${Rubric.points[rubric] ?: "-"}")
            Text("Calculated grade: ${marksText.toIntOrNull()?.let(Rubric::grade) ?: "-"}")
            Button({
                message = if (marksText.isBlank()) "Enter marks." else "Saved locally in DEMO/TEST mode. Connect Firebase for production sync."
            }) { Text("Save mark") }
            if (message.isNotEmpty()) Text(message)
            Spacer(Modifier.height(12.dp))
            Text("Production backend: Firebase Firestore. See README for setup.", style = MaterialTheme.typography.labelSmall)
        }
    }

    @Composable
    fun Results() {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Results & Ranking", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Ranking is permission-controlled in production.")
                Spacer(Modifier.height(12.dp))
            }
            items(demoLearners) { l ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(l.name, fontWeight = FontWeight.Bold)
                        Text("${l.admission} • ${l.grade}")
                        Text("Total rubric points: — / 72")
                    }
                }
            }
        }
    }

    @Composable
    fun More() {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item { Text("More", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            items(listOf(
                "Report Cards", "Bulk Report Cards", "Teacher Management", "Notifications",
                "Teaching Resources", "Analytics", "CSV/PDF Export", "School Settings",
                "Backup & Restore", "Audit Log"
            )) { item ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(item, Modifier.padding(18.dp), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
