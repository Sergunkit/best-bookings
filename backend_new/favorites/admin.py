from django.contrib import admin
from .models import Favorite

@admin.register(Favorite)
class FavoriteAdmin(admin.ModelAdmin):
    list_display = ('user', 'hotel')
    list_filter = ('user', 'hotel')
    search_fields = ('user__username', 'hotel__name')
