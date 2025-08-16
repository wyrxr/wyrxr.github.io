"""
Author: Nathaniel Ingle
Date: 03-August-2025

URL paths for user accounts
"""

from django.urls import path
from .views import SignUpView

urlpatterns = [
    path("signup/", SignUpView.as_view(), name="signup"),
]