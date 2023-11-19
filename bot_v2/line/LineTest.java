package bot_v2.line;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {
    @Test
    void simplePieceTest() {
        Line line = new Line();
        line.addPiece(0, Line.CellType.Ranged);
        line.addPiece(4, Line.CellType.King);
        assertTrue(line.inCheck());
        line.addPiece(3, Line.CellType.Blocking);
        assertFalse(line.inCheck());
        line.remPiece(3);
        assertTrue(line.inCheck());
    }

    @Test
    void compoundPieceTest() {
        Line line = new Line();
        line.addPiece(0, Line.CellType.Ranged);
        line.addPiece(4, Line.CellType.King);
        line.addPiece(7, Line.CellType.Ranged);
        assertTrue(line.inCheck());
        line.addPiece(3, Line.CellType.Blocking);
        assertTrue(line.inCheck());
        line.addPiece(6, Line.CellType.Blocking);
        assertFalse(line.inCheck());
        line.addPiece(5, Line.CellType.Ranged);
        assertTrue(line.inCheck());
        line.remPiece(3);
        assertTrue(line.inCheck());
        line.remPiece(0);
        assertTrue(line.inCheck());
        line.remPiece(5);
        assertFalse(line.inCheck());
        line.addPiece(3, Line.CellType.Blocking);
        assertFalse(line.inCheck());
        line.addPiece(0, Line.CellType.Ranged);
        assertFalse(line.inCheck());
        line.remPiece(6);
        assertTrue(line.inCheck());
    }

    @Test
    void removeEmptyTest(){
        Line line = new Line();
        line.addPiece(3, Line.CellType.King);
        assertThrows(IllegalArgumentException.class, () -> line.remPiece(2));
    }

    @Test
    void addDoubleTest(){
        Line line = new Line();
        line.addPiece(3, Line.CellType.King);
        assertThrows(IllegalArgumentException.class, () -> line.remPiece(2));
    }

    @Test
    void moveKingTest(){
        Line line = new Line();
        line.addPiece(3, Line.CellType.King);
        assertFalse(line.inCheck());
        line.addPiece(6, Line.CellType.Ranged);
        assertTrue(line.inCheck());
        line.addPiece(2, Line.CellType.Blocking);
        assertTrue(line.inCheck());
        line.remPiece(3);
        assertFalse(line.inCheck());
        line.addPiece(1, Line.CellType.King);
        assertFalse(line.inCheck());
        assertThrows(IllegalArgumentException.class, () -> line.addPiece(5, Line.CellType.King));
    }
}