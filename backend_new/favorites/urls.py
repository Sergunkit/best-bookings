from django.urls import path
from .views import UserFavoritesListView, FavoriteView

urlpatterns = [
    path('users/<int:user_id>/favorites/', UserFavoritesListView.as_view(), name='user-favorites-list'),
    path('favorites/', FavoriteView.as_view(), name='favorite-create'),
    path('favorites/<int:hotel_id>/', FavoriteView.as_view(), name='favorite-delete'),
]
