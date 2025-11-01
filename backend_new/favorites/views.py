from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated
from hotels.models import Hotel
from hotels.serializers import HotelSerializer
from hotel_booking.pagination import CustomPagination
from .models import Favorite
from .serializers import FavoriteSerializer


class UserFavoritesListView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, user_id, *args, **kwargs):
        if request.user.is_authenticated and request.user.id == user_id:
            # Get IDs of hotels favorited by the user
            favorite_hotel_ids = Favorite.objects.filter(user_id=user_id).values_list('hotel_id', flat=True)

            # Filter hotels based on the favorite IDs
            hotels = Hotel.objects.filter(id__in=favorite_hotel_ids)

            paginator = CustomPagination()
            paginated_hotels = paginator.paginate_queryset(hotels, request, view=self)
            serializer = HotelSerializer(paginated_hotels, many=True)

            return paginator.get_paginated_response(serializer.data)
        return Response({"detail": "Not authenticated or unauthorized"}, status=status.HTTP_401_UNAUTHORIZED)


class FavoriteView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, *args, **kwargs):
        serializer = FavoriteSerializer(data={'user': request.user.id, 'hotel': request.data.get('hotelId')})
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, *args, **kwargs):
        hotel_id = request.data.get('hotelId')
        try:
            favorite = Favorite.objects.get(user=request.user, hotel_id=hotel_id)
            favorite.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except Favorite.DoesNotExist:
            return Response({"detail": "Favorite not found."}, status=status.HTTP_404_NOT_FOUND)