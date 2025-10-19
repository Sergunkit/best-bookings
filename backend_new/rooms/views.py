from rest_framework import viewsets
from .models import Room
from .serializers import RoomSerializer

class RoomViewSet(viewsets.ModelViewSet):
    serializer_class = RoomSerializer

    def get_queryset(self):
        hotel_id = self.kwargs.get('hotel_pk')
        return Room.objects.filter(hotel=hotel_id)
