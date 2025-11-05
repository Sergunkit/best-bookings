from rest_framework import serializers
from .models import Hotel
from favorites.models import Favorite

class HotelSerializer(serializers.ModelSerializer):
    photoSrc = serializers.CharField(source='photo_src')
    is_favorite = serializers.SerializerMethodField()

    class Meta:
        model = Hotel
        fields = ['id', 'name', 'description', 'rating', 'stars', 'photoSrc', 'is_favorite']

    def get_is_favorite(self, obj):
        user = self.context['request'].user
        if user.is_authenticated:
            return Favorite.objects.filter(hotel=obj, user=user).exists()
        return False
