from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import UserViewSet, UserProfileViewSet

router = DefaultRouter()
router.register(r'users', UserViewSet)
router.register(r'profiles', UserProfileViewSet)

urlpatterns = [
    path('users', UserViewSet.as_view({'get': 'list', 'post': 'create'}), name='user-list'),
    path('users/me', UserViewSet.as_view({'get': 'me'}), name='user-me-no-slash'),
    path('', include(router.urls)),
]
