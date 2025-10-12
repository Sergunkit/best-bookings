from rest_framework import serializers
from .models import Hotel

class HotelSerializer(serializers.ModelSerializer):
    photoSrc = serializers.CharField(source='photo_src')
    class Meta:
        model = Hotel
        fields = ['id', 'name', 'description', 'rating', 'stars', 'photoSrc']
