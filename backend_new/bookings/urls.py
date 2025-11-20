from django.urls import path, include
from rest_framework.routers import DefaultRouter
# from .views import BookingViewSet, FavoriteViewSet
from .views import BookingViewSet


router = DefaultRouter()
router.register(r'bookings', BookingViewSet)
# router.register(r'favorites', FavoriteViewSet)

urlpatterns = [
    path('', include(router.urls)),
]
