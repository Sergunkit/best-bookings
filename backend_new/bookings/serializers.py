from rest_framework import serializers
from .models import Booking, Favorite
from rooms.models import Room

class BookingReadSerializer(serializers.ModelSerializer):
    """
    Serializer for reading bookings. Outputs a flat structure with roomId
    to conform to the OpenAPI specification.
    """
    roomId = serializers.IntegerField(source='room.id', read_only=True)
    checkIn = serializers.DateTimeField(source='check_in', read_only=True)
    checkOut = serializers.DateTimeField(source='check_out', read_only=True)

    class Meta:
        model = Booking
        fields = ['id', 'roomId', 'checkIn', 'checkOut', 'user']
        read_only_fields = ['user']

class BookingWriteSerializer(serializers.ModelSerializer):
    """
    Serializer for writing bookings, accepts IDs for relationships.
    """
    roomId = serializers.PrimaryKeyRelatedField(queryset=Room.objects.all(), source='room', write_only=True)
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
