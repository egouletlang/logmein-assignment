
from util import Client
from test_base import BaseTest

class TestGamesStats(BaseTest):

    def calc_score(self, hand):
        total = 0
        for val in hand.get('abbr', []):
            if 'A' in val:
                total += 1
            elif 'J' in val:
                total += 11
            elif 'Q' in val:
                total += 12
            elif 'K' in val:
                total += 13
            else:
                total += int(val[:-1])

        return total

    # GET /games/{game_id}/score

    def test_getGamesStats_checkScore(self):
        game_id, p1, p2 = self.setup_game()
        self.add_deck(game_id)

        self.shuffle_deck(game_id)

        self.deal_deck(game_id, count=6)

        status, response = self.client.get('/games/%s/score' % (game_id))

        server_score = {}
        for score in response:
            server_score[score.get('player').get('id')] = score.get('score')

        status, p1_hand = self.client.get('/games/%s/players/%s/hand' % (game_id, p1))
        status, p2_hand = self.client.get('/games/%s/players/%s/hand' % (game_id, p2))

        p1_score = self.calc_score(p1_hand)
        p2_score = self.calc_score(p2_hand)

        self.assertEqual(p1_score, server_score.get(p1))
        self.assertEqual(p2_score, server_score.get(p2))


    def test_getGamesStats_checkScore_invalidGameId(self):
        game_id, p1, p2 = self.setup_game()
        self.add_deck(game_id)

        self.shuffle_deck(game_id)

        self.deal_deck(game_id, count=6)

        status, response = self.client.get('/games/%s/score' % (self.randomString()))
        self.assertEqual(status, 404)


    