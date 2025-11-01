from django.db import models
from users.models import User
from hotels.models import Hotel

class Favorite(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='favorites')
    hotel = models.ForeignKey(Hotel, on_delete=models.CASCADE, related_name='favorited_by')

    class Meta:
        unique_together = ('user', 'hotel')

    def __str__(self):
        return f'{self.user} likes {self.hotel}'
