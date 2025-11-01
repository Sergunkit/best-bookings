from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

class MessageListView(APIView):
    def get(self, request, user_id, *args, **kwargs):
        # Заглушка: возвращаем пустой список сообщений
        return Response([], status=status.HTTP_200_OK)

class WebSocketInfoView(APIView):
    def get(self, request, *args, **kwargs):
        # Заглушка для /api/ws/info: возвращаем пустой ответ, чтобы фронтенд не пытался установить WebSocket
        return Response({}, status=status.HTTP_200_OK)
