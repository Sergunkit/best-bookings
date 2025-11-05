from rest_framework import viewsets
from django_filters.rest_framework import DjangoFilterBackend
from hotels.custom_filters import CustomOrderingFilter
from .models import Room
from .serializers import RoomSerializer, BookedDateSerializer
from .filters import RoomFilterBackend
import logging
from rest_framework.response import Response
from rest_framework.decorators import action
from bookings.models import Booking
import datetime

logger = logging.getLogger(__name__)

class RoomViewSet(viewsets.ModelViewSet):
    serializer_class = RoomSerializer
    filter_backends = (DjangoFilterBackend, RoomFilterBackend, CustomOrderingFilter)

    def get_queryset(self):
        hotel_id = self.kwargs.get('hotel_pk')
        if hotel_id:
            return Room.objects.filter(hotel=hotel_id)
        return Room.objects.all()

    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = self.get_serializer(instance)
        return Response(serializer.data)

    @action(detail=True, methods=['get'])
    def availability(self, request, pk=None):
        room = self.get_object()
        start_date_str = request.query_params.get('start')
        end_date_str = request.query_params.get('end')

        if not start_date_str or not end_date_str:
            return Response({'error': 'start' and 'end parameters are required'}, status=400)

        try:
            start_date = datetime.datetime.fromisoformat(start_date_str.replace('Z', '+00:00')).date()
            end_date = datetime.datetime.fromisoformat(end_date_str.replace('Z', '+00:00')).date()
        except ValueError:
            return Response({'error': 'Invalid date format. Use ISO 8601.'}, status=400)

        bookings = Booking.objects.filter(
            room=room,
            check_in__lt=end_date,
            check_out__gt=start_date
        )

        booked_dates = set()
        for booking in bookings:
            current_date = booking.check_in.date()
            while current_date < booking.check_out.date():
                booked_dates.add(current_date)
                current_date += datetime.timedelta(days=1)

        available_dates = []
        current_date = start_date
        while current_date <= end_date:
            if current_date not in booked_dates:
                available_dates.append(current_date.isoformat())
            current_date += datetime.timedelta(days=1)

        return Response({'dates': available_dates})

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
