package ru.mephi.prepod;

public interface Views {
    interface Department {
        interface Full {
        }
    }

    interface Group {
        interface WithParent {
        }

        interface Full extends WithParent {
        }
    }

    interface Institute {
        interface Full {
        }
    }

    interface Lesson {
        interface Full {
        }
    }

    interface Location {
        interface Full {
        }
    }

    interface Professor {
        interface Full {
        }
    }

    interface Student {
        interface Full {
        }
    }
}
