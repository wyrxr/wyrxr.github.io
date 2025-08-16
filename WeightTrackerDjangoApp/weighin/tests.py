"""
Author: Nathaniel Ingle
Date: 20-July-2025

Test suite for weigh in app
"""
import datetime

from django.contrib.auth import get_user_model
from django.test import TestCase
from django.urls import reverse

from .models import WeighIn

class WeighInTest(TestCase):
    @classmethod
    def setUpTestData(cls):
        cls.user = get_user_model().objects.create_user(
            username="testuser", email="test@email.com", password="password123"
        )

        cls.weighin = WeighIn.objects.create(
            user=cls.user,
            date=datetime.date.today(),
            weight=200,
        )

    def test_weighin_model(self):
        # Ensure our test user was correctly created with username, email, and password
        self.assertEqual(self.weighin.user.username, "testuser")
        self.assertEqual(self.weighin.user.email, "test@email.com")
        self.assertTrue(self.weighin.user.check_password("password123"))

        # Ensure that the WeighIn object was correctly created
        self.assertEqual(self.weighin.date, datetime.date.today())
        self.assertEqual(self.weighin.weight, 200)
        self.assertEqual(self.weighin.get_absolute_url(), "/weighin/1/")

    # Test that the homepage can be fetched
    def test_url_exists_at_correct_location_list(self):
        response = self.client.get("/")
        self.assertEqual(response.status_code, 200)

    # Test if the detail fragments can be fetched
    def test_url_exists_at_correct_location_detail(self):
        response = self.client.get("/weighin/1/")
        self.assertEqual(response.status_code, 200)

    # Test that the home routing and template display as expected
    def test_weighin_list(self):
        response = self.client.get(reverse("home"))
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, self.weighin.date.strftime("%B %d, %Y"))
        self.assertTemplateUsed(response, "home.html")

    # Test if the weigh in details routing and template display as expected
    def test_weighin_detail(self):
        response = self.client.get(reverse("weighin_detail", kwargs={"pk": self.weighin.pk}))
        no_response = self.client.get("/weighin/80808080/")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(no_response.status_code, 404)
        self.assertContains(response, str(self.weighin.weight))
        self.assertTemplateUsed(response, "weighin_detail.html")

    # Test whether we can create new weigh ins
    def test_weighin_create(self):
        response = self.client.post(
            reverse("weighin_new"),
            {
                "user": self.user.id,
                "date": datetime.date.today(),
                "weight": 200,
            },
        )
        self.assertEqual(response.status_code, 302)
        self.assertEqual(WeighIn.objects.last().weight, 200)
        self.assertEqual(WeighIn.objects.last().date, datetime.date.today())

    # Test whether we can edit existing weigh ins
    def test_weighin_update(self):
        response = self.client.post(
            reverse("weighin_edit", args="1"),
            {
                "weight": 150,
                "date": datetime.date.today(),
            }
        )
        self.assertEqual(response.status_code, 302)
        self.assertEqual(WeighIn.objects.last().weight, 150)
        self.assertEqual(WeighIn.objects.last().date, datetime.date.today())

    # Test if we can delete existing weigh ins
    def test_weighin_delete(self):
        response = self.client.post(reverse("weighin_delete", args="1"))
        self.assertEqual(response.status_code, 302)
