from rest_framework import serializers
from .models import Booking, Favorite
from rooms.models import Room

class BookingSerializer(serializers.ModelSerializer):
    roomId = serializers.PrimaryKeyRelatedField(queryset=Room.objects.all(), source='room')
    checkIn = serializers.DateTimeField(source='check_in')
    checkOut = serializers.DateTimeField(source='check_out')

    class Meta:
        model = Booking
        fields = ['id', 'roomId', 'checkIn', 'checkOut', 'user']
        read_only_fields = ['user']

class FavoriteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Favorite
        fields = '__all__'
