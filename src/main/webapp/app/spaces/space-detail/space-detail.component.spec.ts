import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpaceDetailComponent } from './space-detail.component';

describe('SpaceDetailComponent', () => {
  let component: SpaceDetailComponent;
  let fixture: ComponentFixture<SpaceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpaceDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SpaceDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
