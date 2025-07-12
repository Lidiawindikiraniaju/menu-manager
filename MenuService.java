package uas;

import java.util.ArrayList;
import java.util.List;

public class MenuService {

    private List<Menu> daftarMenu = new ArrayList<>();
    private int nextId = 1;

    public void tambahMenu(String nama, String kategori, int harga, String ketersediaan) {
        Menu menu = new Menu(nextId++, nama, kategori, harga, ketersediaan);
        daftarMenu.add(menu);
    }

    public List<Menu> getAll() {
        return daftarMenu;
    }

    public Menu cariById(int id) {
        return daftarMenu.stream()
                         .filter(m -> m.getId() == id)
                         .findFirst()
                         .orElse(null);
    }

    public boolean updateMenu(int id, String namaBaru, String kategoriBaru, int hargaBaru, String ketersediaanBaru) {
        Menu menu = cariById(id);
        if (menu != null) {
            menu.setNama(namaBaru);
            menu.setKategori(kategoriBaru);
            menu.setHarga(hargaBaru);
            menu.setKetersediaan(ketersediaanBaru);
            return true;
        }
        return false;
    }

    public boolean hapusMenu(int id) {
        Menu menu = cariById(id);
        return menu != null && daftarMenu.remove(menu);
    }
}
