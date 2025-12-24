import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarVideojuegoComponent } from './editar-videojuego-component';

describe('EditarVideojuegoComponent', () => {
  let component: EditarVideojuegoComponent;
  let fixture: ComponentFixture<EditarVideojuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarVideojuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditarVideojuegoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
