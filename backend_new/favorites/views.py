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
            hotels = Hotel.objects.filter(favorited_by__user_id=user_id).order_by('id')

            serializer = HotelSerializer(hotels, many=True, context={'request': request})

            return Response({
                'data': serializer.data,
                'pagination': {
                    'page': 1,
                    'perPage': len(serializer.data),
                    'totalPages': 1,
                    'total': len(serializer.data),
                }
            })
        return Response({"detail": "Not authenticated or unauthorized"}, status=status.HTTP_401_UNAUTHORIZED)


class FavoriteView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, *args, **kwargs):
        hotel_id = request.data.get('hotelId')
        if not hotel_id:
            return Response({"detail": "hotelId is required."}, status=status.HTTP_400_BAD_REQUEST)

        favorite, created = Favorite.objects.get_or_create(user=request.user, hotel_id=hotel_id)

        hotel = Hotel.objects.get(pk=hotel_id)
        serializer = HotelSerializer(hotel, context={'request': request})

        if created:
            return Response(serializer.data, status=status.HTTP_201_CREATED)

        return Response(serializer.data, status=status.HTTP_200_OK)

    def delete(self, request, hotel_id, *args, **kwargs):
        try:
            favorite = Favorite.objects.get(user=request.user, hotel_id=hotel_id)
            favorite.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        except Favorite.DoesNotExist:
            return Response({"detail": "Favorite not found."}, status=status.HTTP_404_NOT_FOUND)
