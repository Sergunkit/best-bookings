from rest_framework.decorators import action
from rest_framework import viewsets
from django_filters.rest_framework import DjangoFilterBackend
from .models import Hotel
from .serializers import HotelSerializer
from .custom_filters import CustomFilterBackend, CustomOrderingFilter
import logging

logger = logging.getLogger(__name__)

class HotelViewSet(viewsets.ModelViewSet):
    queryset = Hotel.objects.all()
    serializer_class = HotelSerializer
    filter_backends = (DjangoFilterBackend, CustomFilterBackend, CustomOrderingFilter)

    def get_serializer_context(self):
        context = super().get_serializer_context()
        context.update({"request": self.request})
        return context

    def retrieve(self, request, *args, **kwargs):
        logger.info(f"Retrieving hotel with pk={kwargs['pk']}")
        try:
            return super().retrieve(request, *args, **kwargs)
        except Exception as e:
            logger.error(f"Error retrieving hotel: {e}")
            raise

    # вариант без вложенных маршрутов (для каждого типа запроса свой):
    # @action(detail=True, methods=['get'])
    # def rooms(self, request, pk=None):
    #     hotel = self.get_object()
    #     rooms = Room.objects.filter(hotel=hotel)
    #     serializer = RoomSerializer(rooms, many=True)
    #     return Response(serializer.data)
