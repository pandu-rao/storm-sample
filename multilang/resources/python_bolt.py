from __future__ import absolute_import, print_function, unicode_literals
import storm


class PythonBolt(storm.BasicBolt):
    def process(self, tup):
        input_ = tup.values[0]
        ret_info = tup.values[1]

        storm.emit([input_.upper(), ret_info])


if __name__ == '__main__':
    PythonBolt().run()
