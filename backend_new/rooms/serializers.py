from rest_framework import serializers
from .models import Room
from amenities.serializers import AmenitySerializer

class RoomSerializer(serializers.ModelSerializer):
    amenities = AmenitySerializer(many=True, read_only=True)

    class Meta:
        model = Room
        fields = '__all__'
