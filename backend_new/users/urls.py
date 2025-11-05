from django.urls import path, include
from rest_framework_nested import routers
from .views import UserViewSet, UserProfileViewSet
from bookings.views import BookingViewSet

router = routers.DefaultRouter()
router.register(r'users', UserViewSet, basename='user')
router.register(r'profiles', UserProfileViewSet)

users_router = routers.NestedDefaultRouter(router, r'users', lookup='user')
users_router.register(r'bookings', BookingViewSet, basename='user-bookings')

urlpatterns = [
    path('users', UserViewSet.as_view({'get': 'list', 'post': 'create'}), name='user-list'),
    path('users/me', UserViewSet.as_view({'get': 'me'}), name='user-me-no-slash'),
    path('', include(router.urls)),
    path('', include(users_router.urls)),
]
