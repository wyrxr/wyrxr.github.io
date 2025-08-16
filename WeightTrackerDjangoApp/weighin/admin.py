"""
Author: Nathaniel Ingle
Date: 20-July-2025

Admin panel for the weigh in app.
"""

from django.contrib import admin
from .models import WeighIn


# Create an admin class to view weigh ins
class WeighInAdmin(admin.ModelAdmin):
    list_display = ('date', 'weight')

# Register the WeighIn and WeighInAdmin models
admin.site.register(WeighIn, WeighInAdmin)
