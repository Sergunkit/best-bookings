from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.permissions import IsAuthenticated
from hotels.models import Hotel
from hotels.serializers import HotelSerializer

class UserFavoritesListView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, user_id, *args, **kwargs):
        # Заглушка: возвращаем список отелей, если пользователь авторизован
        if request.user.is_authenticated and request.user.id == user_id:
            # В реальном приложении здесь была бы логика получения избранных отелей пользователя
            # Для заглушки, просто возвращаем все отели
            hotels = Hotel.objects.all()
            serializer = HotelSerializer(hotels, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response({"detail": "Not authenticated or unauthorized"}, status=status.HTTP_401_UNAUTHORIZED)