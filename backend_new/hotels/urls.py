from django.urls import path, include
from rest_framework_nested import routers
from .views import HotelViewSet
from rooms.views import RoomViewSet

router = routers.DefaultRouter()
router.register(r'hotels', HotelViewSet, basename='hotel')

hotels_router = routers.NestedDefaultRouter(router, r'hotels', lookup='hotel')
hotels_router.register(r'rooms', RoomViewSet, basename='hotel-rooms')

urlpatterns = [
    path('', include(router.urls)),
    path('', include(hotels_router.urls)),
]

# Здесь rooms/ является вложенным маршрутом для hotels/ 
# Сделано с помощью доп. библиотеки drf-nested-routers (в коде rest_framework_nested)
