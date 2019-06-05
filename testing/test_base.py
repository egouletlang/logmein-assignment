import unittest
import json
import uuid

from util import Client


client = Client('localhost', '8080')

class BaseTest(unittest.TestCase):

    GAME_NAME = 'test-game'
    PLAYER_USERNAME = 'test-player'

    @property
    def client(self):
        return client

    def randomString(self):
        return str(uuid.uuid4())
    
    def setUp(self):
        self.player_count = 0
        self.delete_test_game()
        self.delete_test_players()

    def tearDown(self):
        self.player_count = 0
        self.delete_test_game()
        self.delete_test_players()

    # Helpers
    def setup_game(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()
        player_2_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)
        self.add_player_to_game(game_id, player_2_id)

        return game_id, player_1_id, player_2_id

    def add_deck(self, game_id):
        self.client.post('/games/%s/deck/add' % game_id)

    def shuffle_deck(self, game_id):
        self.client.post('/games/%s/deck/shuffle' % game_id)

    def deal_deck(self, game_id, count=5):
        self.client.post('/games/%s/deck/deal' % game_id, {'count': count})

    def add_player_to_game(self, gameId, playerId):
        status, response = self.client.post('/games/%s/players' % gameId, {'id' : playerId})

    def create_test_game(self):
        status, response = self.client.post('games', {'name': BaseTest.GAME_NAME})
        return response.get('id')

    def delete_test_game(self):
        status, response = self.client.get('games', {'name': BaseTest.GAME_NAME})
        if response and len(response) > 0:
            status, response = self.client.delete('games/%s' % response[0].get('id'))

    def create_test_player(self):
        username = BaseTest.PLAYER_USERNAME + str(self.player_count)
        status, response = self.client.post('players', {'username': username, 'firstName': 'TestPlayer', 'lastName': self.player_count})
        self.player_count += 1
        return response.get('id')

    def delete_test_players(self):
        status, response = self.client.get('players')
        
        for player in response:
            if BaseTest.PLAYER_USERNAME in player.get('username'):
                self.client.delete('players/%s' % player.get('id'))

