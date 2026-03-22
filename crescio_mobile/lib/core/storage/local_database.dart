import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';

class LocalDatabase {
  static final LocalDatabase instance = LocalDatabase._();
  static Database? _database;

  LocalDatabase._();

  Future<Database> get database async {
    _database ??= await _openDatabase();
    return _database!;
  }

  Future<Database> _openDatabase() async {
    final dir = await getApplicationDocumentsDirectory();
    final path = p.join(dir.path, 'cresio_mobile.db');
    return openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE mantenimientos_pendientes (
            id TEXT PRIMARY KEY,
            equipo_id INTEGER,
            tipo_mantenimiento TEXT,
            descripcion TEXT,
            odoo_ticket_id TEXT,
            checklist_json TEXT,
            repuestos_json TEXT,
            fecha_creacion TEXT,
            sincronizado INTEGER DEFAULT 0
          )
        ''');
        await db.execute('''
          CREATE TABLE fotos_pendientes (
            id TEXT PRIMARY KEY,
            mantenimiento_local_id TEXT,
            ruta_local TEXT,
            sincronizado INTEGER DEFAULT 0
          )
        ''');
        await db.execute('''
          CREATE TABLE firmas_pendientes (
            id TEXT PRIMARY KEY,
            mantenimiento_local_id TEXT,
            tipo_firma TEXT,
            firma_base64 TEXT,
            sincronizado INTEGER DEFAULT 0
          )
        ''');
      },
    );
  }

  Future<void> insertarMantenimientoPendiente(Map<String, Object?> data) async {
    final db = await database;
    await db.insert(
      'mantenimientos_pendientes',
      data,
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  Future<List<Map<String, Object?>>> listarPendientes() async {
    final db = await database;
    return db.query(
      'mantenimientos_pendientes',
      where: 'sincronizado = ?',
      whereArgs: [0],
      orderBy: 'fecha_creacion ASC',
    );
  }

  Future<void> marcarSincronizado(String id) async {
    final db = await database;
    await db.update(
      'mantenimientos_pendientes',
      {'sincronizado': 1},
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  Future<int> contarPendientes() async {
    final db = await database;
    final result = await db.rawQuery(
      'SELECT COUNT(*) AS total FROM mantenimientos_pendientes WHERE sincronizado = 0',
    );
    return Sqflite.firstIntValue(result) ?? 0;
  }
}
