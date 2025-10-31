from django.urls import path
from .views import UserFavoritesListView

urlpatterns = [
    path('users/<int:user_id>/favorites', UserFavoritesListView.as_view(), name='user-favorites-list'),
]
