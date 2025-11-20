from rest_framework import serializers
from .models import Room
from amenities.serializers import AmenitySerializer
from bookings.models import Booking
from hotels.serializers import HotelSerializer

class RoomSerializer(serializers.ModelSerializer):
    amenities = AmenitySerializer(many=True, read_only=True)
    hotelId = serializers.IntegerField(source='hotel.id')

    class Meta:
        model = Room
        fields = ['id', 'name', 'description', 'price', 'amenities', 'hotelId']

class BookedDateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Booking
        fields = ['check_in', 'check_out']
