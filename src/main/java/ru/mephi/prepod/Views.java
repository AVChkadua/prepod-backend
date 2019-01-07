package ru.mephi.prepod;

public interface Views {
    interface Bell {
    }

    interface Department {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Group {
        interface Basic {
        }

        interface WithParent extends Basic {
        }

        interface Full extends WithParent {
        }
    }

    interface Institute {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Lesson {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Location {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Position {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Professor {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Student {
        interface Basic {
        }

        interface Full extends Basic {
        }
    }

    interface Subject {
    }
}
