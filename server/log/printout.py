class Printout:
    INFO_STYLE = '\033[0;37m[INFO]'
    ERROR_STYLE = '\033[1;31m[ERROR]'
    WARNING_STYLE = '\033[0;33m[WARNING]'
    @staticmethod
    def i(key, message):
        print(f'{Printout.INFO_STYLE} {key}: {message}')
    @staticmethod
    def e(key, message):
        print(f'{Printout.ERROR_STYLE} {key}: {message}')
    @staticmethod
    def w(key, message):
        print(f'{Printout.WARNING_STYLE} {key}: {message}')