package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.FeedViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MyApplicationApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun MyApplicationApp() {
    // Поточна вкладка: rememberSaveable — не скидається при повороті екрана
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.COUNTER) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)
            when (currentDestination) {
                AppDestinations.COUNTER -> CounterNavHost(screenModifier)   // ЛР2: навігація всередині вкладки
                AppDestinations.GREETING -> GreetingScreen(screenModifier)
                AppDestinations.STATUS -> StatusScreen(screenModifier)
                AppDestinations.DATABASE -> DatabaseScreen(screenModifier)   // ЛР4: Room
            }
        }
    }
}

// Власні елементи навігації (ЛР1, завдання 5)
enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    COUNTER("Лічильник", R.drawable.ic_home),
    GREETING("Привітання", R.drawable.ic_favorite),
    STATUS("Статус", R.drawable.ic_account_box),
    DATABASE("База", R.drawable.ic_storage),            // ЛР4
}

/* ===================== ЛР2: навігація в межах вкладки «Лічильник» ===================== */

// Маршрути підекранів
object CounterRoutes {
    const val MAIN = "counter_main"
    const val DETAILS = "counter_details/{count}"
    fun details(count: Int) = "counter_details/$count"
}

@Composable
fun CounterNavHost(modifier: Modifier = Modifier) {
    // NavController зберігає back stack підекранів і обробляє системну кнопку/жест «Назад»
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CounterRoutes.MAIN,
        modifier = modifier
    ) {
        // Підекран 1: розмітка з ЛР1 + кнопка переходу
        composable(CounterRoutes.MAIN) {
            CounterScreen(
                onOpenDetails = { count -> navController.navigate(CounterRoutes.details(count)) }
            )
        }
        // Підекран 2: довільна розмітка, отримує аргумент count
        composable(
            route = CounterRoutes.DETAILS,
            arguments = listOf(navArgument("count") { type = NavType.IntType })
        ) { backStackEntry ->
            val count = backStackEntry.arguments?.getInt("count") ?: 0
            CounterDetailsScreen(
                count = count,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/* ---------- Підекран 1: remember vs rememberSaveable (по центру) ---------- */
@Composable
fun CounterScreen(
    modifier: Modifier = Modifier,
    onOpenDetails: (Int) -> Unit = {}
) {
    var countRemember by remember { mutableIntStateOf(0) }          // скидається при повороті
    var countSaveable by rememberSaveable { mutableIntStateOf(0) }  // зберігається при повороті

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "remember: $countRemember", fontSize = 22.sp)
        Text(
            text = "rememberSaveable: $countSaveable",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                countRemember++
                countSaveable++
            },
            modifier = Modifier.size(width = 220.dp, height = 56.dp)
        ) {
            Text("Натисни +1")
        }
        Spacer(Modifier.height(12.dp))
        // ЛР2: кнопка переходу на другий підекран
        OutlinedButton(
            onClick = { onOpenDetails(countSaveable) },
            modifier = Modifier.width(220.dp)
        ) {
            Text("Детальніше →")
        }
    }
}

/* ---------- Підекран 2: деталі лічильника (довільна розмітка) ---------- */
@Composable
fun CounterDetailsScreen(
    count: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
            Text(text = "Деталі", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Значення лічильника", fontSize = 16.sp)
                Text(
                    text = "$count",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when {
                        count == 0 -> "Ви ще не натискали кнопку"
                        count % 2 == 0 -> "Число парне"
                        else -> "Число непарне"
                    },
                    fontSize = 16.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Повернутися можна кнопкою «Назад» угорі, системною кнопкою або жестом.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/* ---------- Екран 2: ViewModel (зверху зліва) ---------- */
class GreetingViewModel : ViewModel() {
    private val greetings = listOf("Привіт!", "Hello!", "Hola!", "Bonjour!", "Ciao!")
    private var index = 0

    var text by mutableStateOf(greetings[0])
        private set
    var clicks by mutableIntStateOf(0)
        private set

    fun next() {
        index = (index + 1) % greetings.size
        text = greetings[index]
        clicks++
    }
}

@Composable
fun GreetingScreen(
    modifier: Modifier = Modifier,
    vm: GreetingViewModel = viewModel(),
    feedVm: FeedViewModel = viewModel()   // ЛР3: дані для списку
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = vm.text, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text(text = "Натискань (ViewModel): ${vm.clicks}", fontSize = 16.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { vm.next() }, modifier = Modifier.fillMaxWidth()) {
            Text("Змінити привітання")
        }
        Spacer(Modifier.height(16.dp))
        Text(text = "Стрічка", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        // ЛР3: список займає весь вільний простір, що залишився (weight = 1f)
        FeedList(
            items = feedVm.items,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

/* ---------- Екран 3: remember + зміна кольору (знизу справа) ---------- */
@Composable
fun StatusScreen(modifier: Modifier = Modifier) {
    var isOnline by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isOnline) "Онлайн" else "Офлайн",
                fontSize = 28.sp,
                color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            FilledTonalButton(
                onClick = { isOnline = !isOnline },
                modifier = Modifier.width(150.dp)
            ) {
                Text("Перемкнути")
            }
        }
    }
}
