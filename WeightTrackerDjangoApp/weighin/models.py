"""
Author: Nathaniel Ingle
Date: 20-July-2025

Model for the weigh-in database
"""
from datetime import datetime

from django.db import models
from django.urls import reverse
from django.utils import timezone

class WeighIn(models.Model):
    # The user this weigh in belongs to
    user = models.ForeignKey("auth.User", on_delete=models.CASCADE)
    # The date and time of the weigh in
    date = models.DateField(default=timezone.now)
    # The recorded weight
    weight = models.FloatField()

    def __str__(self):
        return str(self.date) + ": " + str(self.weight)

    def get_absolute_url(self):
        return reverse('weighin_detail', kwargs={'pk': self.pk})
