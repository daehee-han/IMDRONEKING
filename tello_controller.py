import socket
import threading
import time
import traceback


class Tello:

    def __init__(self, local_ip, local_port, imperial=True, command_timeout=.3, tello_ip='192.168.10.1', tello_port=8889):
        self.abort_flag = False
        self.command_timeout = command_timeout
        self.imperial = imperial
        self.response = None
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.tello_address = (tello_ip, tello_port)

        self.socket.bind((local_ip, local_port))

        self.receive_thread = threading.Thread(target=self._receive_thread)
        self.receive_thread.daemon=True

        self.receive_thread.start()

        if self.send_command('command') != 'OK':
            raise RuntimeError('Tello rejected attempt to enter command mode')

    def __del__(self):
        self.socket.close()

    def _receive_thread(self):
        while True:
            try:
                self.response, ip = self.socket.recvfrom(256)
            except Exception:
                break

    def flip(self, direction):
        return self.send_command('flip %s' % direction)

    def get_battery(self):
        battery = self.send_command('battery?')
        try:
            battery = int(battery)
        except Exception:
            pass

        return battery

    def get_flight_time(self):
        flight_time = self.send_command('time?')

        try:
            flight_time = int(flight_time)
        except Exception:
            pass

        return flight_time

    def get_speed(self):
        speed = self.send_command('speed?')

        try:
            speed = float(speed)

            if self.imperial is True:
                speed = round((speed / 44.704), 1)
            else:
                speed = round((speed / 27.7778), 1)
        except Exception:
            pass

        return speed

    def land(self):
        return self.send_command('land')

    def move(self, direction, distance):
        distance = float(distance)

        if self.imperial is True:
            distance = int(round(distance * 30.48))
        else:
            distance = int(round(distance * 100))

        return self.send_command('%s %s' % (direction, distance))

    def move_backward(self, distance):
        return self.move('back', distance)

    def move_down(self, distance):
        return self.move('down', distance)

    def move_forward(self, distance):
        return self.move('forward', distance)

    def move_left(self, distance):
        return self.move('left', distance)

    def move_right(self, distance):
        return self.move('right', distance)

    def move_up(self, distance):
        return self.move('up', distance)

    def send_command(self, command):
        self.abort_flag = False
        timer = threading.Timer(self.command_timeout, self.set_abort_flag)

        self.socket.sendto(command.encode('utf-8'), self.tello_address)

        timer.start()

        while self.response is None:
            if self.abort_flag is True:
                raise RuntimeError('No response to command')

        timer.cancel()

        response = self.response.decode('utf-8')
        self.response = None

        return response

    def set_abort_flag(self):
        self.abort_flag = True

    def set_speed(self, speed):
        speed = float(speed)

        if self.imperial is True:
            speed = int(round(speed * 44.704))
        else:
            speed = int(round(speed * 27.7778))

        return self.send_command('speed %s' % speed)

    def takeoff(self):
        return self.send_command('takeoff')

    def rotate_cw(self, degrees):
        return self.send_command('cw %s' % degrees)

    def rotate_ccw(self, degrees):
        return self.send_command('ccw %s' % degrees)