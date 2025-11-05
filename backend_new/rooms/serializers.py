from rest_framework import serializers
from .models import Room
from amenities.serializers import AmenitySerializer
from bookings.models import Booking

class RoomSerializer(serializers.ModelSerializer):
    amenities = AmenitySerializer(many=True, read_only=True)

    class Meta:
        model = Room
        fields = '__all__'

class BookedDateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Booking
        fields = ['check_in', 'check_out']
