from django.urls import path
from .views import MessageListView, WebSocketInfoView

urlpatterns = [
    path('messages/<int:user_id>', MessageListView.as_view(), name='message-list'),
    path('ws/info', WebSocketInfoView.as_view(), name='websocket-info'),
]
