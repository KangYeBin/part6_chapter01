package com.yb.part6_chapter01.viewmodel.order

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yb.part6_chapter01.data.entity.OrderEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.repository.order.DefaultOrderRepository
import com.yb.part6_chapter01.data.repository.order.OrderRepository
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.screen.order.OrderMenuListState
import com.yb.part6_chapter01.screen.order.OrderMenuListViewModel
import com.yb.part6_chapter01.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.Mockito

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class OrderMenuListViewModelTest : ViewModelTest() {

    @Mock
    lateinit var firebaseAuth: FirebaseAuth

    @Mock
    lateinit var firebaseUser: FirebaseUser

    private val orderMenuListViewModel by inject<OrderMenuListViewModel> {
        parametersOf(firebaseAuth)
    }

    private val restaurantFoodRepository by inject<RestaurantFoodRepository>()

    private val orderRepository by inject<OrderRepository>()

    private val restaurantId = 0L
    private val restaurantTitle = "식당명"

    @Before
    fun `insert food menu in basket`() = runBlockingTest {
        (0 until 10).forEach {
            restaurantFoodRepository.insertFoodMenuInBasket(
                RestaurantFoodEntity(
                    id = it.toString(),
                    title = "메뉴 $it",
                    description = "소개 $it",
                    price = it,
                    imageUrl = "",
                    restaurantId,
                    restaurantTitle
                )
            )
        }
    }

    @Test
    fun `test load order menu list`() = runBlockingTest {
        val testObservable = orderMenuListViewModel.orderMenuListStateLiveData.test()
        orderMenuListViewModel.fetchData()

        testObservable.assertValueSequence(
            listOf(
                OrderMenuListState.UnInitialized,
                OrderMenuListState.Loading,
                OrderMenuListState.Success(
                    restaurantFoodRepository.getAllFoodMenuListInBasket().map {
                        FoodModel(
                            id = it.hashCode().toLong(),
                            type = CellType.ORDER_FOOD_CELL,
                            title = it.title,
                            description = it.description,
                            price = it.price,
                            imageUrl = it.imageUrl,
                            restaurantId = it.restaurantId,
                            foodId = it.id,
                            restaurantTitle = it.restaurantTitle
                        )
                    }
                )
            )
        )
    }

    @Test
    fun `test do order menu list`() = runBlockingTest {
        `insert food menu in basket`()

        val userId = "asdf"
        Mockito.`when`(firebaseAuth.currentUser).then { firebaseUser }
        Mockito.`when`(firebaseUser.uid).then { userId }

        val testObservable = orderMenuListViewModel.orderMenuListStateLiveData.test()
        orderMenuListViewModel.fetchData()

        val menuListInBasket = restaurantFoodRepository.getAllFoodMenuListInBasket().map { it.copy() }

        val menuListInBasketModel = menuListInBasket.map {
            FoodModel(
                id = it.hashCode().toLong(),
                type = CellType.ORDER_FOOD_CELL,
                title = it.title,
                description = it.description,
                price = it.price,
                imageUrl = it.imageUrl,
                restaurantId = it.restaurantId,
                foodId = it.id,
                restaurantTitle = it.restaurantTitle
            )
        }

        orderMenuListViewModel.orderMenu()

        testObservable.assertValueSequence(
            listOf(
                OrderMenuListState.UnInitialized,
                OrderMenuListState.Loading,
                OrderMenuListState.Success(
                    restaurantFoodModelList = menuListInBasketModel
                ),
                OrderMenuListState.Order
            )
        )

        assert(orderRepository.getAllOrderMenus(userId) is DefaultOrderRepository.Result.Success<*>)

        val result = (orderRepository.getAllOrderMenus(userId) as DefaultOrderRepository.Result.Success<*>).data

        assert(
            result?.equals(
                listOf(
                    OrderEntity(
                        id = 0.toString(),
                        userId = userId,
                        restaurantId = restaurantId,
                        foodMenuList = menuListInBasket,
                        restaurantTitle = restaurantTitle
                    )
                )
            ) ?: false
        )

    }
}