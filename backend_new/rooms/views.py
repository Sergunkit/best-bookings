from rest_framework import viewsets
from django_filters.rest_framework import DjangoFilterBackend
from hotels.custom_filters import CustomOrderingFilter
from .models import Room
from .serializers import RoomSerializer
from .filters import RoomFilterBackend
import logging
from rest_framework.response import Response

logger = logging.getLogger(__name__)

class RoomViewSet(viewsets.ModelViewSet):
    serializer_class = RoomSerializer
    filter_backends = (DjangoFilterBackend, RoomFilterBackend, CustomOrderingFilter)

    def get_queryset(self):
        hotel_id = self.kwargs.get('hotel_pk')
        return Room.objects.filter(hotel=hotel_id)

    def list(self, request, *args, **kwargs):
        hotel_id = self.kwargs.get('hotel_pk')
        logger.info(f"Listing rooms for hotel with pk={hotel_id}")
        try:
            queryset = self.filter_queryset(self.get_queryset())
            
            page = self.paginate_queryset(queryset)
            if page is not None:
                serializer = self.get_serializer(page, many=True)
                return self.get_paginated_response(serializer.data)

            serializer = self.get_serializer(queryset, many=True)
            return Response({'data': serializer.data})
        except Exception as e:
            logger.error(f"Error listing rooms: {e}")
            raise
