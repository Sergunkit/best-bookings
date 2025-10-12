from django.db import models

class Hotel(models.Model):
    name = models.CharField(max_length=255)
    description = models.TextField()
    rating = models.FloatField()
    stars = models.IntegerField()
    photo_src = models.CharField(max_length=255)

    def __str__(self):
        return self.name